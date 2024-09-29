package com.dette.service;

import com.dette.model.Client;
import com.dette.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public void sendFidelityCardWithQRCode(Client client) throws IOException {
        try {
            // Vérifiez que l'utilisateur existe et que son email est présent
            if (client.getUser() == null || client.getUser().getEmail() == null) {
                throw new RuntimeException("L'adresse e-mail de l'utilisateur est manquante.");
            }

            // Générer le QR code à partir des informations du client
            String qrCodeText = "ID du client : " + client.getId() + "\nNom : " + client.getNom();
            byte[] qrCodeImage = qrCodeService.generateQRCode(qrCodeText);

            // Créer le message MIME
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(client.getUser().getEmail());
            helper.setSubject("Votre carte de fidélité avec QR code");
            helper.setText("Bonjour " + client.getNom() + ",\n\n" +
                    "Merci pour votre inscription. Veuillez trouver ci-joint votre carte de fidélité avec un QR code.");

            // Joindre le QR code en tant qu'image
            ByteArrayResource qrCodeAttachment = new ByteArrayResource(qrCodeImage);
            helper.addAttachment("carte_fidelite_qr_code.png", qrCodeAttachment);

            // Envoyer l'e-mail
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Échec de l'envoi de l'e-mail", e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur inattendue lors de l'envoi de la carte de fidélité", e);
        }
    }
}
