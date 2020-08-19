package com.scalahome.common.closablepool;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.scalahome.common.MutableOptional;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author fuqing.xu
 * @date 2020-07-11 17:21
 */
@Slf4j
public class CloseablePool {

    private final Cache<String, CloseableWrapper> cache;

    public CloseablePool(long duration, TimeUnit unit) {
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(duration, unit)
                .removalListener(new RemovalListener<String, CloseableWrapper>() {

                    @Override
                    public void onRemoval(RemovalNotification<String, CloseableWrapper> notification) {
                        try {
                            log.info("onRemoval:" + notification);
                            CloseableWrapper closeableWrapper = notification.getValue();
                            if (closeableWrapper != null) {
                                closeableWrapper.close();
                            }
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                }).build();
    }

    public void close() {
        cache.cleanUp();
    }

    public void closeResource(String key) {
        log.info("closeResource:" + key);
        cache.invalidate(key);
    }

    private <T> T getResource0(String key, Callable<CloseableWrapper<T>> callable) throws ExecutionException {
        return (T) cache.get(key, callable).getResource();
    }

    public <T> T getResource(String key, Callable<T> callable) throws ExecutionException {
        return getResource0(key, new Callable<CloseableWrapper<T>>() {
            @Override
            public CloseableWrapper<T> call() throws Exception {
                MutableOptional<T> mutableOptional = new MutableOptional<>();
                return new CloseableWrapper<>(mutableOptional.orElseCall(() -> callable.call()), new Closeable() {
                    @Override
                    public void close() throws IOException {
                        mutableOptional.ifPresent(new Consumer<T>() {
                            @Override
                            public void accept(T t) {
                                try {
                                    if (t instanceof Closeable) {
                                        ((Closeable) t).close();
                                    }
                                } catch (IOException e) {
                                    log.error(e.getMessage(), e);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
