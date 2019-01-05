package com.danikula.videocache.bandwidth;

/**
 * 带宽限制
 * xihuan22d
 */
public class BandwidthLimiter {
    private final int KB = 1024;
    private final int MIN_CHUNK_LENGTH = 1024;//最小计数块长度（以字节为单位）
    private final int MIN_SLEEP_TIME = 10 * 1000000;//最小睡眠时间,Thread.sleep()在安卓设备上误差较大,所以设置10毫秒以上才睡一次
    private int bytesWillBeSentOrReceive = 0;
    private long lastPieceSentOrReceiveTick = System.nanoTime();//最后一个块被发送或接收的时间
    private int maxRate = 1024;
    private long timeCostPerChunk = (1000000000L * MIN_CHUNK_LENGTH) / (maxRate * KB);//以纳秒为单位发送MIN_CHUNK_LENGTH字节的时间成本
    private long needSleepTime = 0;//以纳秒为单位

    public BandwidthLimiter(int maxRate) {
        setMaxRate(maxRate);
    }

    /**
     * 设置最大上载或下载速率，以KB/s为单位.如果maxRate为小于等于0,则表示没有带宽限制
     */
    public void setMaxRate(int maxRate) {
        synchronized (this) {
            if (maxRate < 0) {
                maxRate = 0;
            }
            if (maxRate == 0) {
                timeCostPerChunk = 0;
            } else {
                timeCostPerChunk = (1000000000L * MIN_CHUNK_LENGTH) / (maxRate * KB);
            }
        }
    }

    /**
     * 如果需要对BandwidthLimiter进行复用的话,进行下载之前需要重置最后发送的时间,以免missedTime出现大额的负数,影响带宽限制功能
     */
    public void resetSentOrReceiveTick() {
        synchronized (this) {
            lastPieceSentOrReceiveTick = System.nanoTime();
        }
    }

    /**
     * 接下来的1字节长度的字节需要做带宽限制
     */
    public void limitNextBytes() {
        limitNextBytes(1);
    }

    /**
     * 接下来的len长度的字节需要做带宽限制
     */
    public void limitNextBytes(int len) {
        synchronized (this) {
            if (timeCostPerChunk != 0) {
                bytesWillBeSentOrReceive = bytesWillBeSentOrReceive + len;
                while (bytesWillBeSentOrReceive > MIN_CHUNK_LENGTH) {//http请求读取的长度建议和MIN_CHUNK_LENGTH长度设置的一致,不然missedTime计算将很不准确
                    long nowTick = System.nanoTime();
                    long missedTime = timeCostPerChunk - (nowTick - lastPieceSentOrReceiveTick);
                    needSleepTime = needSleepTime + missedTime;
                    if (needSleepTime >= MIN_SLEEP_TIME) {
                        try {
                            Thread.sleep(needSleepTime / 1000000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        needSleepTime = 0;
                    }
                    bytesWillBeSentOrReceive = bytesWillBeSentOrReceive - MIN_CHUNK_LENGTH;
                    lastPieceSentOrReceiveTick = nowTick + missedTime;
                }
            }
        }
    }
}