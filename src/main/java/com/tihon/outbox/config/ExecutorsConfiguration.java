package com.tihon.outbox.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorsConfiguration {
    @Value("${numberOfThreads}")
    private int numberOfThreads;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService outboxThreadPool() {
        return Executors.newFixedThreadPool(numberOfThreads);
    }
}
