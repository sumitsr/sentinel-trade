package com.bank.sentinel;

import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.Executors;

@SpringBootApplication
public class SentinelTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelTradeApplication.class, args);
    }

    @Bean
    public TomcatProtocolHandlerCustomizer<?> virtualThreadCustomizer() {
        return (ProtocolHandler handler) ->
            handler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
    }
}
