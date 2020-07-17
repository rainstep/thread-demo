package com.example.threaddemo.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class LockConsumerThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(LockConsumerThread.class);

    private String consumerName;
    private Queue<String> workQueue;
    private Lock lock;
    private Condition condition;

    public LockConsumerThread(String consumerName, Queue<String> workQueue, Lock lock, Condition condition) {
        super(consumerName +"-thread");
        this.consumerName = consumerName;
        this.workQueue = workQueue;
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void run() {
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
    }
}
