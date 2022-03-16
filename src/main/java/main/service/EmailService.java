package main.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;

    public void sendRestorePasswordLink(String to, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password restore");
        message.setText(text);
        emailSender.send(message);
    }
}
