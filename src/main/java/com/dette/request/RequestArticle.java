package com.dette.request;

import com.dette.validation.UniqueField;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RequestArticle {

    @NotBlank(message = "Le libellé ne peut pas être vide")
    @UniqueField(fieldName = "libeller", message = "Le libellé doit être unique") // Utilisation de l'annotation générique
    private String libeller;

    @NotNull(message = "La quantité ne peut pas être nulle")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantite;

    @NotNull(message = "Le prix ne peut pas être nul")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private double prix;

    // Constructeur par défaut
    public RequestArticle() {
    }

    // Constructeur avec paramètres
    public RequestArticle(String libeller, Integer quantite, double prix) {
        this.libeller = libeller;
        this.quantite = quantite;
        this.prix = prix;
    }

    // Getters et Setters
    public String getLibeller() {
        return libeller;
    }

    public void setLibeller(String libeller) {
        this.libeller = libeller;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Méthode toString
    @Override
    public String toString() {
        return "RequestArticle{" +
                "libeller='" + libeller + '\'' +
                ", quantite=" + quantite +
                ", prix=" + prix +
                '}';
    }
}
