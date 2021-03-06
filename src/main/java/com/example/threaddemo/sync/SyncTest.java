package com.example.threaddemo.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SyncTest {
    private static final Logger logger = LoggerFactory.getLogger(SyncTest.class);

    public static void main(String[] args) {
        int messageCount = 1000;
        test(messageCount);
    }

    public static void test(int messageCount) {
        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";

        new SyncConsumerThread(consumer1, workQueue).start();
        new SyncConsumerThread(consumer2, workQueue).start();

        // 生产和通知消费
        produceAndNotifyConsumer(workQueue, messageCount);
    }

    public static void test2(int messageCount) {
        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";

        startConsumer(consumer1, workQueue);
        startConsumer(consumer2, workQueue);

        produceAndNotifyConsumer(workQueue, messageCount);
    }

    private static void startConsumer(String consumerName, Queue<String> workQueue) {
        new Thread(() -> {
            while (true) {
                synchronized (workQueue) {
                    if (workQueue.isEmpty()) {
                        try {
                            workQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String message = workQueue.poll();
                        logger.info("{}消费消息：{}", consumerName, message);
                    }
                }
            }
        }, consumerName + "-thread").start();
    }

    private static void produceAndNotifyConsumer(Queue<String> workQueue, int messageCount) {
        for (int i = 0; i < messageCount; i++) {
            String message = "message" + (i + 1);
            logger.info("生产消息：{}", message);
            workQueue.offer(message);
            // 一有消息就通知消费者
//            logger.info("通知消费者");
//            synchronized (workQueue) {
//                workQueue.notifyAll();
//            }
        }
        // 所有消息生产完才通知消费者
        logger.info("通知消费者");
        synchronized (workQueue) {
            workQueue.notifyAll();
        }
    }
}
