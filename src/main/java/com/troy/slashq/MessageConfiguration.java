package com.troy.slashq;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MessageConfiguration {

    private final MessageRepository messageRepository;

    @PostConstruct
    public void init() {

        for(int i = 0; i < 100 ; i++){
            int finalI = i;
            messageRepository.registerConsumerToTopic(""+finalI, c -> {
                if (c != null) {
                    log.info("Topic-" + finalI + " is handled : " + c.getMessage() + " Thread : " + Thread.currentThread().getName());
                }
            });

        }
    }



}
