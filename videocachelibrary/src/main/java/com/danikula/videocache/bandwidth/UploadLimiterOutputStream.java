package com.danikula.videocache.bandwidth;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 上传带宽限制
 * xihuan22d
 */
public class UploadLimiterOutputStream extends OutputStream {
    private OutputStream outputStream;
    private BandwidthLimiter bandwidthLimiter;

    public UploadLimiterOutputStream(OutputStream outputStream, BandwidthLimiter bandwidthLimiter) {
        this.outputStream = outputStream;
        this.bandwidthLimiter = bandwidthLimiter;
    }

    @Override
    public void write(int b) throws IOException {
        if (bandwidthLimiter != null) {
            bandwidthLimiter.limitNextBytes();
        }
        outputStream.write(b);
    }

    public void write(byte[] bytes, int off, int len) throws IOException {
        if (bandwidthLimiter != null) {
            bandwidthLimiter.limitNextBytes(len);
        }
        outputStream.write(bytes, off, len);
    }

}