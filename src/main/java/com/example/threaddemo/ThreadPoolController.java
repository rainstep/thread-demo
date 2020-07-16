package com.example.threaddemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

@RestController
@RequestMapping("/threadPool")
public class ThreadPoolController {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolController.class);

    @GetMapping("/test")
    public void test(@RequestParam(defaultValue = "100") int count,
                      @RequestParam(defaultValue = "10") int threadCount) throws InterruptedException {

        int corePoolSize = threadCount;
        int maximumPoolSize = 100;
        long keepAliveTime = 5 * 60;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }
}
