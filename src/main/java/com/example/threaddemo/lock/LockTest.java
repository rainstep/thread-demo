package com.example.threaddemo.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {
    private static final Logger logger = LoggerFactory.getLogger(LockTest.class);

    public static void main(String[] args) {
        test2(10);
    }

    public static void test(int messageCount) {
        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();

        String consumer1 = "consumer1";
        String consumer2 = "consumer2";

        Lock lock = new ReentrantLock();

        new ConsumerThread(consumer1, workQueue, lock).start();
        new ConsumerThread(consumer2, workQueue, lock).start();

        // 生产和通知消费
        produceAndNotifyConsumer(workQueue, messageCount, lock);

    }

    public static void test2(int messageCount) {
        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();
        Lock lock = new ReentrantLock();

        startConsumer("consumer1", workQueue, lock);
        startConsumer("consumer2", workQueue, lock);

        produceAndNotifyConsumer(workQueue, messageCount, lock);

    }

    private static void startConsumer(String consumerName, Queue<String> workQueue, Lock lock) {
        new Thread(() -> {
            while (true) {
                lock.lock();
                if (workQueue.isEmpty()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    String message = workQueue.poll();
                    logger.info("{}消费消息：{}", consumerName, message);
                }
                lock.unlock();
            }
        }, consumerName + "-thread").start();
    }

    private static void produceAndNotifyConsumer(Queue<String> workQueue, int messageCount, Lock lock) {
        for (int i = 0; i < messageCount; i++) {
            String message = "message" + (i + 1);
            logger.info("生产消息：{}", message);
            workQueue.offer(message);
            logger.info("通知消费者");
            lock.unlock();
        }
    }
}
