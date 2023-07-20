package com.troy.slashq;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageConfiguration {

    private final MessageRepository messageRepository;

    @PostConstruct
    public void init() {

        for (int i = 0; i < 16; i++) {
            int finalI = i;
            messageRepository.registerConsumerToTopic("" + finalI, new SingleThreadQueueExecutor(c -> {
                log.info("Topic-" + finalI + " is handled : " + c.getMessage() + " Thread : " + Thread.currentThread().getName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));

        }
    }



}
