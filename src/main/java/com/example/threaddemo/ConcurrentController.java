package com.example.threaddemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
@RequestMapping("/concurrent")
public class ConcurrentController {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrentController.class);

    // 模拟并发
    @GetMapping("/test")
    public void test(@RequestParam(defaultValue = "100") int count) throws InterruptedException {
        // 并发数
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                String threadName = Thread.currentThread().getName();
                try {
                    logger.info("线程{}等待中……", threadName);
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("线程{}完成", threadName);
            }, "thread-" + i).start();

            Thread.sleep(10);

            if (i == count - 1) {
                logger.warn("任务开始");
            }

            countDownLatch.countDown();
        }
    }

}
