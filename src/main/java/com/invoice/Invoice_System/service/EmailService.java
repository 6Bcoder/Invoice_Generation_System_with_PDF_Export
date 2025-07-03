package com.invoice.Invoice_System.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;


@Service
public class EmailService {

    @Autowired
    private  JavaMailSender mailSender;

//      //  public EmailService(JavaMailSender mailSender) {
//            this.mailSender = mailSender;
//        }

        public void sendInvoiceEmail(String toEmail, String subject, String body, String pdfPath) throws MessagingException {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            // Attach PDF
            FileSystemResource file = new FileSystemResource(new File(pdfPath));
            helper.addAttachment("invoice.pdf", file);

            mailSender.send(message);
        }
}
