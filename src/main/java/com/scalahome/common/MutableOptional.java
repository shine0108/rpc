package com.scalahome.common;

import lombok.NonNull;
import lombok.Setter;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author fuqing.xu
 * @date 2020-07-11 18:47
 */
public class MutableOptional<T> {

    private static final MutableOptional<?> EMPTY = new MutableOptional<>();

    @Setter
    private volatile T value;

    public MutableOptional() {
    }

    public MutableOptional(@NonNull T value) {
        this.value = value;
    }

    public static <T> MutableOptional<T> of(T value) {
        return new MutableOptional<>(value);
    }

    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T orElseGet(Supplier<? extends T> other) {
        return value != null ? value : other.get();
    }

    public T orElseCall(Callable<T> callable) throws Exception {
        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    value = callable.call();
                }
            }
        }
        return value;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MutableOptional)) {
            return false;
        }

        MutableOptional<?> other = (MutableOptional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
                ? String.format("MutableOptional[%s]", value)
                : "MutableOptional.empty";
    }
}
