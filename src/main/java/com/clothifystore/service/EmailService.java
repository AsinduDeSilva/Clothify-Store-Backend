package com.clothifystore.service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmail(String to, String subject, String emailBody) throws MessagingException, UnsupportedEncodingException;
}
