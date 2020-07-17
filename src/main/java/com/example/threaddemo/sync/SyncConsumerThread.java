package com.example.threaddemo.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

public class SyncConsumerThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SyncConsumerThread.class);

    private String consumerName;
    private Queue<String> workQueue;

    public SyncConsumerThread(String consumerName, Queue<String> workQueue) {
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
