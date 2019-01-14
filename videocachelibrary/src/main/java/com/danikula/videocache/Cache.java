package com.danikula.videocache;

/**
 * Cache for proxy.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 *
 *  add reset
 *  updated by xihuan22d
 */
public interface Cache {

    long available() throws ProxyCacheException;

    int read(byte[] buffer, long offset, int length) throws ProxyCacheException;

    void append(byte[] data, int length) throws ProxyCacheException;

    void reset() throws ProxyCacheException;

    void close() throws ProxyCacheException;

    void complete() throws ProxyCacheException;

    boolean isCompleted();
}
