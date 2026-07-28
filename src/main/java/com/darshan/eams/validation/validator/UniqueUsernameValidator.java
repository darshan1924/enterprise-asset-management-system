package com.darshan.eams.validation.validator;

import com.darshan.eams.validation.annotation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return true;
    }
}