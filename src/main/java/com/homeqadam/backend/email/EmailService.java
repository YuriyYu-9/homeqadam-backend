package com.homeqadam.backend.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("homeqadam@mail.ru");           // ВАЖНО: тот же адрес, что и spring.mail.username
        message.setTo(to);
        message.setSubject("HomeQadam - подтверждение почты");
        message.setText("Ваш код подтверждения: " + code + "\n\nКод действителен 15 минут.");

        mailSender.send(message);
    }
}
