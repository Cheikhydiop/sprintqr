package com.dette.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import com.dette.repository.ArticleRepository;

public class UniqueFieldValidator implements ConstraintValidator<UniqueField, String> {

    @Autowired
    private ArticleRepository articleRepository; // Injection du repository

    private String fieldName;

    @Override
    public void initialize(UniqueField constraintAnnotation) {
        this.fieldName = constraintAnnotation.fieldName(); // Récupérer le nom du champ
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Laissez les autres annotations gérer la validation
        }
        // Exemple : Vérification d'un champ spécifique
        if (fieldName.equals("libeller")) {
            return !articleRepository.existsByLibeller(value); // Remplacez par votre logique
        }
        return true; // Si le champ n'est pas reconnu, retourner true par défaut
    }
}
