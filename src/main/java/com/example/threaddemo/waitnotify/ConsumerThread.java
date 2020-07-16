package com.example.threaddemo.waitnotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class ConsumerThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

    private String consumerName;
    private Queue<String> workQueue;

    public ConsumerThread(String consumerName, Queue<String> workQueue) {
        super(consumerName + "-thread");
        this.consumerName = consumerName;
        this.workQueue = workQueue;
    }

    @Override
    public void run() {
        while (true) {
            synchronized(workQueue) {
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
    }
}
