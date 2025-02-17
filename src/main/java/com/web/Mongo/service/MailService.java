package com.web.Mongo.service;


public interface MailService {
    public void sendVerificationCode(String to, String subject, String text);
}
