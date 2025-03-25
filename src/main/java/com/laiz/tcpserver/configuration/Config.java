package com.laiz.tcpserver.configuration;

import com.laiz.tcpserver.settings.AppDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class Config {
    @Bean
    public ExecutorService tcpServerExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Bean
    public ExecutorService requestHandlerExecutor() {
        return Executors.newFixedThreadPool(AppDefaults.TCP_REQUEST_HANDLER_THREADS_NUMBER);
    }
}