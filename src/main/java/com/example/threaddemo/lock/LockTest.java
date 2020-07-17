package com.example.threaddemo.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {
    private static final Logger logger = LoggerFactory.getLogger(LockTest.class);

    public static void main(String[] args) {
        test2(100);
    }

    public static void test(int messageCount) {
        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();

        String consumer1 = "consumer1";
        String consumer2 = "consumer2";

        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        new LockConsumerThread(consumer1, workQueue, lock, condition).start();
        new LockConsumerThread(consumer2, workQueue, lock, condition).start();

        // 生产和通知消费
        produceAndNotifyConsumer(workQueue, messageCount, lock, condition);

    }

    public static void test2(int messageCount) {
        ConcurrentLinkedQueue<String> workQueue = new ConcurrentLinkedQueue<>();
        Lock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        startConsumer("consumer1", workQueue, lock, condition);
        startConsumer("consumer2", workQueue, lock, condition);

        produceAndNotifyConsumer(workQueue, messageCount, lock, condition);

    }

    private static void startConsumer(String consumerName, Queue<String> workQueue, Lock lock, Condition condition) {
        new Thread(() -> {
            while (true) {
                lock.lock();
                if (workQueue.isEmpty()) {
                    try {
                        condition.await();
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

    private static void produceAndNotifyConsumer(Queue<String> workQueue, int messageCount, Lock lock, Condition condition) {
        for (int i = 0; i < messageCount; i++) {
            String message = "message" + (i + 1);
            logger.info("生产消息：{}", message);
            workQueue.offer(message);
            logger.info("通知消费者");
            // 一有消息就通知消费者
//            lock.lock();
//            condition.signalAll();
//            lock.unlock();
        }
        // 所有消息生产完才通知消费者
        lock.lock();
        condition.signalAll();
        lock.unlock();
    }
}
