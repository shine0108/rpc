package com.scalahome.common.closablepool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author fuqing.xu
 * @date 2020-07-11 16:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloseableWrapper<T> implements Closeable {
    private T resource;
    private Closeable closeable;

    @Override
    public void close() throws IOException {
        if (closeable != null) {
            closeable.close();
        } else if (resource instanceof Closeable) {
            ((Closeable) resource).close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }
}
