package com.redjanvier.signature.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class TestMailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        return new NoOpMailSender();
    }
}
