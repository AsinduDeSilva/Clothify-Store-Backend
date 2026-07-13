package com.clothifystore.service;

import com.clothifystore.entity.User;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface OTPService {
    void sendOTP(User user) throws MessagingException, UnsupportedEncodingException;
    boolean verifyOTP(User user, String otp);
    boolean isOTPExpired(User user);
}
