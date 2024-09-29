package com.dette.model;

import java.io.Serializable;
import java.util.Collection;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Imports pour la génération de code QR
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Entity
@Table(name = "client") 
public class Client implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private long id;

    @Column(name = "nom", nullable = false, length = 20)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 20)
    private String prenom;

    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;

    @NotBlank
    @Size(max = 255) // Ajustez selon vos besoins
    @Column(name = "address", nullable = true)
    private String address;
    
    private String email; // Ensure this field is present

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private Collection<Dette> dettes;

    @Lob 
 

    @Column(name = "qr_code", nullable = true) 
    private String qrCode;

    // Méthode pour générer le code QR
    public String generateQRCode() {
        String clientInfo = "Nom: " + nom + ", Prénom: " + prenom + ", Téléphone: " + telephone + ", Adresse: " + address;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(clientInfo, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] qrCodeBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(qrCodeBytes); // Retourne le code QR en base64
        } catch (WriterException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters et Setters...
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }
    
    
    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Collection<Dette> getDettes() {
        return dettes;
    }

    public void setDettes(Collection<Dette> dettes) {
        this.dettes = dettes;
    }
    
    

    public String getEmail() { // Add this getter method
        return email;
    }

    public void setEmail(String email) { // Add this setter method
        this.email = email;
    }
}
