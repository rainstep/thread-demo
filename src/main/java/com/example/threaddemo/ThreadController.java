package com.example.threaddemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


@RestController
public class ThreadController {
    final int THREAD_COUNT = 10;
    private final Map<Long, Queue<Long>> queueMap = new ConcurrentHashMap<>(THREAD_COUNT);

    // 客户端
    final int SEND_THREAD_COUNT = 10;
    Map<Integer,  List<Integer>> arrMap = new HashMap<>(SEND_THREAD_COUNT);

    @PostConstruct
    public void init() {
        for (long i = 0; i < THREAD_COUNT; i++) {
            ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
            queueMap.put(i, queue);
            new Thread(() -> {
                String name = Thread.currentThread().getName();
                while (true) {
                    if (queue.isEmpty()) {
                        synchronized (queue) {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        System.out.println(name + "(2): " + queue.poll());
                    }

                }
            }, "thread-" + i).start();
        }


        for (int i = 0; i < SEND_THREAD_COUNT; i++) {
            arrMap.put(i, new ArrayList<>());
        }
    }


    private void receive(long num) {
        long key = num % THREAD_COUNT;
        Queue<Long> queue = queueMap.get(key);
        queue.offer(num);
        synchronized (queue) {
            queue.notify();
        }
    }


    @GetMapping("/send")
    public void send(int count) {

        for (int i = 0; i < count; i++) {
            int key = i % SEND_THREAD_COUNT;
            arrMap.get(key).add(i);
        }
        CountDownLatch countDownLatch = new CountDownLatch(SEND_THREAD_COUNT);
        for (int i = 0; i < SEND_THREAD_COUNT; i++) {
            int finalI = i;
            new Thread(() -> {
                List<Integer> arr = arrMap.get(finalI);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String name = Thread.currentThread().getName();
                System.out.println(name + ":" + System.currentTimeMillis());
//                for (int j = 0; j < arr.size(); j++) {
//                    receive(arr.get(j));
//                }
            }, "sendThread-"+finalI).start();
            countDownLatch.countDown();
        }
//        for (int i = 0; i < count; i++) {
//            receive(i);
//        }
    }

    @GetMapping("/test")
    public void test(long i) {
        Queue<Long> queue = queueMap.get(i);
        synchronized (queue) {
            queue.notify();
        }
    }

    @GetMapping("/bf")
    public void bf(int count) {

        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {

        }
    }
}
