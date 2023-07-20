package com.troy.slashq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageRepository {

    private final ReactiveRedisConnectionFactory redisConnectionFactory;

    public MessageRepository(ReactiveRedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;

    }

    public void registerConsumerToTopic(String topicName, SingleThreadQueueExecutor singleThreadQueueExecutor) {

        ReactiveRedisMessageListenerContainer reactiveRedisMessageListenerContainer1 = new ReactiveRedisMessageListenerContainer(redisConnectionFactory);


        reactiveRedisMessageListenerContainer1.receiveLater(ChannelTopic.of(topicName))
                .doOnNext(c ->
                        c
                                .doOnNext(stringStringMessage -> log.info("RECEIVED " + Thread.currentThread().getName()))
                                .subscribe(singleThreadQueueExecutor::execute))
                .subscribe();

        // why subscribe on doesn't works

    }


}
