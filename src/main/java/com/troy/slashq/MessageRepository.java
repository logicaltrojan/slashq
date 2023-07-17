package com.troy.slashq;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageRepository {

    private final ReactiveRedisConnectionFactory redisConnectionFactory;


    public void registerConsumerToTopic(String topicName, Consumer<ReactiveSubscription.Message<String, String>> consumerPipeline){

        ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer1 = new ReactiveRedisMessageListenerContainer(redisConnectionFactory);

        reactiveRedisMessageListenerContainer1.receiveLater(ChannelTopic.of(topicName))
                .doOnNext(c -> c
                        .doOnNext(stringStringMessage -> log.info("RECEIVED " + Thread.currentThread().getName()))
                        .publishOn(Schedulers.newSingle("Worker" + topicName)).subscribe(consumerPipeline))
                // why subscribe on doesn't works
                // there is pre- subscribe on flux ( reactive redis )
                .subscribe();


    }


}
