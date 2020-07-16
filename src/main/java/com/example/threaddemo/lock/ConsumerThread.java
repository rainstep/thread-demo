package com.example.threaddemo.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class ConsumerThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

    private String consumerName;
    private Lock lock;
    private Queue<String> workQueue;

    public ConsumerThread(String consumerName, Queue<String> workQueue, Lock lock) {
        super(consumerName +"-thread");
        this.consumerName = consumerName;
        this.workQueue = workQueue;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true) {
            if (workQueue.isEmpty()) {
                lock.lock();
            } else {
                String message = workQueue.poll();
                logger.info("{}消费消息：{}", consumerName, message);
            }
        }
    }
}
