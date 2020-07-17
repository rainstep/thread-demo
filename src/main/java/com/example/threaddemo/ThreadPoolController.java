package com.example.threaddemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/threadPool")
public class ThreadPoolController {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolController.class);

    @GetMapping("/test")
    public void test(@RequestParam(defaultValue = "100") int count,
                      @RequestParam(defaultValue = "10") int threadCount) throws InterruptedException {

    }

    private ThreadPoolExecutor createThreadPoolExecutor(int threadCount) {
        int maximumPoolSize = 100;
        long keepAliveTime = 5 * 60;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
        return new ThreadPoolExecutor(threadCount, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    final int THREAD_COUNT = 10;
    private final Map<Long, Queue<Long>> queueMap = new ConcurrentHashMap<>(THREAD_COUNT);


    @PostConstruct
    public void init() {
        for (long i = 0; i < THREAD_COUNT; i++) {
            ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
            queueMap.put(i, queue);
            new Thread(() -> {
                while (true) {
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            logger.info("{}", queue.poll());
                        }
                    }
                }
            }, "thread-" + i).start();
        }
    }


    private void receive(long num) {
        long key = num % THREAD_COUNT;
        Queue<Long> queue = queueMap.get(key);
        queue.offer(num);
        synchronized (queue) {
            queue.notify();
        }
    }


    @GetMapping("/bfSend")
    public void bfSend(@RequestParam(defaultValue = "100") long count,
                       @RequestParam(defaultValue = "10") int threadCount) {

        Map<Integer, List<Long>> queueMap = new HashMap<>();

        for (int i = 0; i < threadCount; i++) {
            queueMap.put(i, new ArrayList<>());
        }

        for (long i = 0; i < count; i++) {
            int key = (int) (i % threadCount);
            queueMap.get(key).add(i);
        }

        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        CountDownLatch countDownLatch2 = new CountDownLatch(threadCount);

        ThreadPoolExecutor threadPoolExecutor = createThreadPoolExecutor(threadCount);
        for (int i = 0; i < threadCount; i++) {
            List<Long> list = queueMap.get(i);
            threadPoolExecutor.execute(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < list.size(); j++) {
                    doWork();
                }
                countDownLatch2.countDown();
            });
            countDownLatch.countDown();
        }
        logger.info("开始时间：{}", System.currentTimeMillis());
        try {
            countDownLatch2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("结束时间：{}", System.currentTimeMillis());


    }


    @GetMapping("/send")
    public void send(@RequestParam(defaultValue = "100") int count) {
        logger.info("开始时间：{}", System.currentTimeMillis());
        for (int i = 0; i < count; i++) {
            doWork();
        }
        logger.info("结束时间：{}", System.currentTimeMillis());
    }

    private void doWork() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
