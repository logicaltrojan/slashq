package com.troy.slashq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveSubscription;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class SingleThreadQueueExecutor {

    private final ThreadPoolExecutor threadPoolExecutor;
    private final Consumer<ReactiveSubscription.Message<?, ?>> consumer;

    public SingleThreadQueueExecutor(Consumer<ReactiveSubscription.Message<?, ?>> consumer) {
        this.consumer = consumer;
        this.threadPoolExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.DiscardOldestPolicy());
        //warm up
        this.threadPoolExecutor.execute(() -> {
            log.info("Thread ready :{}", Thread.currentThread().getName());
        });

    }

    public void execute(ReactiveSubscription.Message<?, ?> message) {
        threadPoolExecutor.execute(() -> consumer.accept(message));
    }

}
