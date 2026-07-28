package com.darshan.eams.validation;

import com.darshan.eams.repository.AssetRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueAssetCodeValidator implements ConstraintValidator<UniqueAssetCode, String> {

    private final AssetRepository assetRepository;

    @Override
    public boolean isValid(String assetCode, ConstraintValidatorContext context) {
        if (assetCode == null || assetCode.isBlank())
            return true;
        return !assetRepository.existsByAssetCodeIgnoreCase(assetCode);
    }
}