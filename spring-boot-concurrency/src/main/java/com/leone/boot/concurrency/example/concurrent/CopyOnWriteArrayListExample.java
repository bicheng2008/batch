package com.leone.boot.concurrency.example.concurrent;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author leone
 * @since 2018-05-06
 **/
@Slf4j
public class CopyOnWriteArrayListExample {

    public static int clientTotal = 5000;
    public static int threadTotal = 200;

    private static List<Integer> list = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {

        ExecutorService executorService = Executors.newCachedThreadPool();

        final Semaphore semaphore = new Semaphore(threadTotal);

        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

        for (int i = 0; i < clientTotal; i++) {
            final int count = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    update(count);
                    semaphore.release();
                } catch (InterruptedException e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executorService.shutdown();
        log.info("size:{}", list.size());
    }

    private static void update(int i) {
        list.add(i);
    }

}
