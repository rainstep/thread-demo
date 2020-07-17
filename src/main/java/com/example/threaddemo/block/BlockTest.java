package com.example.threaddemo.block;

import com.example.threaddemo.sync.SyncTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BlockTest {
    private static final Logger logger = LoggerFactory.getLogger(SyncTest.class);

    public static void main(String[] args) {
        int messageCount = 100;
        test(messageCount);
    }

    public static void test(int messageCount) {
        LinkedBlockingQueue<String> workQueue = new LinkedBlockingQueue<>();
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";

        startConsumer(consumer1, workQueue);
        startConsumer(consumer2, workQueue);

        produceAndNotifyConsumer(workQueue, messageCount);
    }

    private static void startConsumer(String consumerName, BlockingQueue<String> workQueue) {
        new Thread(() -> {
            while (true) {
                String message = null;
                try {
                    message = workQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("{}消费消息：{}", consumerName, message);
            }
        }, consumerName + "-thread").start();
    }

    private static void produceAndNotifyConsumer(BlockingQueue<String> workQueue, int messageCount) {
        for (int i = 0; i < messageCount; i++) {
            String message = "message" + (i + 1);
            logger.info("生产消息：{}", message);
            workQueue.offer(message);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
