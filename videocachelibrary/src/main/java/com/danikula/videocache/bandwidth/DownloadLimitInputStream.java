package com.danikula.videocache.bandwidth;

import java.io.IOException;
import java.io.InputStream;

/**
 * 下载带宽限制
 * xihuan22d
 */
public class DownloadLimitInputStream extends InputStream {
    private InputStream inputStream;
    private BandwidthLimiter bandwidthLimiter;

    public DownloadLimitInputStream(InputStream inputStream, BandwidthLimiter bandwidthLimiter) {
        this.inputStream = inputStream;
        this.bandwidthLimiter = bandwidthLimiter;
    }

    @Override
    public int read() throws IOException {
        if (bandwidthLimiter != null) {
            bandwidthLimiter.limitNextBytes();
        }
        return inputStream.read();
    }

    public int read(byte[] bytes, int off, int len) throws IOException {
        if (bandwidthLimiter != null) {
            bandwidthLimiter.limitNextBytes(len);
        }
        return inputStream.read(bytes, off, len);
    }
}