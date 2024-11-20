package com.redjanvier.signature.mock;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import jakarta.mail.internet.MimeMessage;

public class NoOpMailSender implements JavaMailSender {
    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(java.io.InputStream contentStream) {
        return null;
    }

    @Override
    public void send(MimeMessage mimeMessage) {
        System.out.println("No-op: MimeMessage send called.");
    }

    @Override
    public void send(MimeMessage... mimeMessages) {
        System.out.println("No-op: MimeMessage array send called.");
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) {
        System.out.println("No-op: SimpleMailMessage send called.");
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) {
        System.out.println("No-op: SimpleMailMessage array send called.");
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'send'");
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Unimplemented method 'send'");
    }
}
