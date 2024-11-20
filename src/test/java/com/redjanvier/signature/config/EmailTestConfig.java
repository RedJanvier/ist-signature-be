package com.redjanvier.signature.config;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class EmailTestConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class); // Mock JavaMailSender for tests
    }
}