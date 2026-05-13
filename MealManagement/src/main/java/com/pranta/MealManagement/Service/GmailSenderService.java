package com.pranta.MealManagement.Service;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.pranta.MealManagement.Entity.AdminGoogleToken;
import com.pranta.MealManagement.Repository.AdminGoogleTokenRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class GmailSenderService {

    private final AdminGoogleTokenRepository tokenRepository;

    public void sendMemberPasswordEmail(
            String adminEmail,
            String memberEmail,
            String temporaryPassword
    ) throws Exception {

        AdminGoogleToken token = tokenRepository.findByAdminEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin Gmail is not connected"));

        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod())
                .setAccessToken(token.getAccessToken());

        Gmail gmail = new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential
        )
                .setApplicationName("Meal Management System")
                .build();

        MimeMessage email = createEmail(
                memberEmail,
                adminEmail,
                "Your Meal Management Account Password",
                "Hello,\n\nYour account has been created.\n\n"
                        + "Email: " + memberEmail + "\n"
                        + "Temporary Password: " + temporaryPassword + "\n\n"
                        + "Please login and update your password immediately.\n\n"
                        + "Thank you."
        );

        Message message = createMessageWithEmail(email);

        gmail.users().messages().send("me", message).execute();
    }

    private MimeMessage createEmail(
            String to,
            String from,
            String subject,
            String bodyText
    ) throws Exception {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }

    private Message createMessageWithEmail(MimeMessage email) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);

        byte[] bytes = buffer.toByteArray();

        String encodedEmail = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        return message;
    }
}