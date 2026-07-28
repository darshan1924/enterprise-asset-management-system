package com.darshan.eams.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startField = constraintAnnotation.startField();
        this.endField = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null)
            return true;

        Field startFieldRef = ReflectionUtils.findField(dto.getClass(), startField);
        Field endFieldRef = ReflectionUtils.findField(dto.getClass(), endField);

        if (startFieldRef == null || endFieldRef == null)
            return true;

        ReflectionUtils.makeAccessible(startFieldRef);
        ReflectionUtils.makeAccessible(endFieldRef);

        Object startValue = ReflectionUtils.getField(startFieldRef, dto);
        Object endValue = ReflectionUtils.getField(endFieldRef, dto);

        if (!(startValue instanceof LocalDate start) || !(endValue instanceof LocalDate end))
            return true;
        return !end.isBefore(start);
    }
}