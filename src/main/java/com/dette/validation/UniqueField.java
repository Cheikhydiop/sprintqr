package com.dette.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation personnalisée générique pour valider l'unicité d'un champ
@Constraint(validatedBy = UniqueFieldValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {

    String message() default "Ce champ doit être unique";
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String fieldName(); // Nom du champ à valider
}
