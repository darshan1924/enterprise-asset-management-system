package com.darshan.eams.validation.annotation;

import com.darshan.eams.validation.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message() default "Password is not strong enough.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}