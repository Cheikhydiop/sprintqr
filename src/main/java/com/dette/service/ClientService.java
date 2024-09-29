package com.dette.service;

import com.dette.event.FidelityCardEvent;
import com.dette.model.Client;
import com.dette.repository.ClientRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);
    
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    public Client saveClient(Client client) {
        validateClient(client); // Appel à la méthode de validation
        
        try {
            Client savedClient = clientRepository.save(client);
            logger.info("Client enregistré avec succès: {}", savedClient.getNom());
            eventPublisher.publishEvent(new FidelityCardEvent(this, savedClient));
            return savedClient;
        } catch (DataAccessException e) {
            logger.error("Erreur lors de l'enregistrement du client : {}", e.getMessage());
            throw new RuntimeException("Erreur lors de l'enregistrement du client", e);
        }
    }

    public List<Client> getAllClients() {
        logger.info("Récupération de tous les clients.");
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
        logger.info("Client supprimé avec ID : {}", id);
    }

    @Transactional
    public Client updateClient(Client client) {
        validateClient(client);
        return clientRepository.save(client);
    }

    private void validateClient(Client client) {
        if (client.getNom() == null || client.getNom().isEmpty()) {
            logger.error("Validation échouée : le nom du client est vide.");
            throw new IllegalArgumentException("Le nom du client ne peut pas être vide.");
        }

        if (client.getEmail() == null || !client.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            logger.error("Validation échouée : l'email est invalide.");
            throw new IllegalArgumentException("L'email du client est invalide.");
        }

        if (clientRepository.existsByEmail(client.getEmail())) {
            logger.error("Validation échouée : client avec cet email existe déjà.");
            throw new IllegalArgumentException("Un client avec cet email existe déjà.");
        }

        if (client.getQrCode() == null) {
            client.setQrCode(generateQrCode());
        }
    }

    private String generateQrCode() {
        return UUID.randomUUID().toString();
    }

    private String generateQrCodeGraphic(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

            byte[] qrCodeBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (WriterException | IOException e) {
            logger.error("Erreur lors de la génération du QR code : {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }
}
