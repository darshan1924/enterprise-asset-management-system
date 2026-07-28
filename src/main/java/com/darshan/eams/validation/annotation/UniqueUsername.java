package com.darshan.eams.validation.annotation;

import com.darshan.eams.validation.validator.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {

    String message() default "Username already exists.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}