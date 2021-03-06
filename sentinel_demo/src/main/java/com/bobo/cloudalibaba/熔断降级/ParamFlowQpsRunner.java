package com.bobo.cloudalibaba.熔断降级;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.csp.sentinel.util.TimeUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wuxiaobo@didachuxing.com
 * @create 2018-11-29 21:23
 **/
public class ParamFlowQpsRunner<T> {

    private final T[] params;
    private final String resourceName;
    private int seconds;
    private final int threadCount;

    private final Map<T, AtomicLong> passCountMap = new ConcurrentHashMap<>();

    private volatile boolean stop = false;

    public ParamFlowQpsRunner(T[] params, String resourceName, int threadCount, int seconds) {
        assertTrue(params != null && params.length > 0, "Parameter array should not be empty");
        assertTrue(StringUtil.isNotBlank(resourceName), "Resource name cannot be empty");
        assertTrue(seconds > 0, "Time period should be positive");
        assertTrue(threadCount > 0 && threadCount <= 1000, "Invalid thread count");
        this.params = params;
        this.resourceName = resourceName;
        this.seconds = seconds;
        this.threadCount = threadCount;

        for (T param : params) {
            assertTrue(param != null, "Parameters should not be null");
            passCountMap.putIfAbsent(param, new AtomicLong());
        }
    }

    private void assertTrue(boolean b, String message) {
        if (!b) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Pick one of provided parameters randomly.
     *
     * @return picked parameter
     */
    private T generateParam() {
        int i = ThreadLocalRandom.current().nextInt(0, params.length);
        return params[i];
    }

    public void simulateTraffic() {
        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(new RunTask());
            t.setName("sentinel-simulate-traffic-task-" + i);
            t.start();
        }
    }

    public void tick() {
        Thread timer = new Thread(new TimerTask());
        timer.setName("sentinel-timer-task");
        timer.start();
    }

    private void passFor(T param) {
        passCountMap.get(param).incrementAndGet();
    }

    final class RunTask implements Runnable {
        @Override
        public void run() {
            while (!stop) {
                Entry entry = null;

                try {
                    T param = generateParam();
                    entry = SphU.entry(resourceName, EntryType.IN, 1, param);
                    // Add pass for parameter.
                    passFor(param);
                } catch (BlockException e1) {
                    // block.incrementAndGet();
                } catch (Exception e2) {
                    // biz exception
                } finally {
                    // total.incrementAndGet();
                    if (entry != null) {
                        entry.exit();
                    }
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(0, 10));
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    final class TimerTask implements Runnable {
        @Override
        public void run() {
            long start = System.currentTimeMillis();
            System.out.println("Begin to run! Go go go!");
            System.out.println("See corresponding metrics.log for accurate statistic data");

            Map<T, Long> map = new HashMap<>(params.length);
            for (T param : params) {
                map.putIfAbsent(param, 0L);
            }
            while (!stop) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
                // There may be a mismatch for time window of internal sliding window.
                // See corresponding `metrics.log` for accurate statistic log.
                for (T param : params) {
                    long globalPass = passCountMap.get(param).get();
                    long oldPass = map.get(param);
                    long oneSecondPass = globalPass - oldPass;
                    map.put(param, globalPass);
                    System.out.println(String.format("[%d][%d] Hot param metrics for resource %s: "
                                    + "pass count for param <%s> is %d",
                            seconds, TimeUtil.currentTimeMillis(), resourceName, param, oneSecondPass));
                }
                if (seconds-- <= 0) {
                    stop = true;
                }
            }

            long cost = System.currentTimeMillis() - start;
            System.out.println("Time cost: " + cost + " ms");
            System.exit(0);
        }
    }
}
