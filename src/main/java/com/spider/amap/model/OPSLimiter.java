package com.spider.amap.model;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 高德接口访问频率
 *
 * @author zhangjun
 * @date 2018/12/5
 */
public class OPSLimiter {

    private Semaphore semaphore = null;

    public OPSLimiter(int maxOps) {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() ->
                semaphore.release(maxOps), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public void await() {
        semaphore.acquireUninterruptibly(1);
    }
}
