package com.sfm.obd.entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOtpMessage(String to, String subject, String text, String username) throws MessagingException {




        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("no.reply.cfl@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Bonjour Mr/Mrs " + username +",\n" +
                "Votre code est  " + text+"  . \n Le code expirera dans 4 minutes." +
                "\n Merci." +
                "\n L'equipe CFL.");


        javaMailSender.send(message);
    }

    public void sendResetPasswordMessage(String to, String subject, String text, String username) throws MessagingException {




        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("no.reply.cfl@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Bonjour Mr/Mrs " + username +",\n" +
                text+"  . \n Le lien expirera dans une heure." +
                "\n Merci." +
                "\n L'equipe CFL.");


        javaMailSender.send(message);
    }
}
