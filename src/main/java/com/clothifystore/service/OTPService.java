package com.clothifystore.service;

import com.clothifystore.entity.User;
import com.clothifystore.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    private final Random random = new Random();

    public void sendOTP(User user) throws MessagingException, UnsupportedEncodingException {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        user.setOtp(passwordEncoder.encode(otp.toString()));
        user.setOtpExpirationTime(LocalDateTime.now().plusMinutes(5));
        userRepo.save(user);

        String emailBody =
                "<div style=\"font-family: Helvetica,Arial,sans-serif;min-width:1000px;overflow:auto;line-height:2\">\n" +
                "  <div style=\"margin:50px auto;width:70%;padding:20px 0\">\n" +
                "    <div style=\"border-bottom:1px solid #eee\">\n" +
                "      <a href=\"\" style=\"font-size:1.4em;color: #000000;text-decoration:none;font-weight:600\">Clothify Store</a>\n" +
                "    </div>\n" +
                "    <p>Use the following OTP to complete your Sign Up procedures. OTP is valid for 5 minutes</p>\n" +
                "    <h2 style=\"background: #000000;margin: 0 auto;width: max-content;padding: 0 10px;color: #fff;border-radius: 4px;\">"+otp+"</h2>\n" +
                "    <hr style=\"border:none;border-top:1px solid #eee\" />\n" +
                "    <div style=\"float:right;padding:8px 0;color:#aaa;font-size:0.8em;line-height:1;font-weight:300\">\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>";

        emailService.sendEmail(user.getEmail(), "Customer Account Confirmation", emailBody);
    }

    public boolean verifyOTP(User user, String otp){
        if (!passwordEncoder.matches(otp, user.getOtp())){return false;}
        user.setOtp(null);
        user.setOtpExpirationTime(null);
        userRepo.save(user);
        return true;
    }

    public boolean isOTPExpired(User user){
        return LocalDateTime.now().isAfter(user.getOtpExpirationTime());
    }

}
