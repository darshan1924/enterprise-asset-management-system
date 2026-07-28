package com.darshan.eams.exception.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
public class ValidationErrorResponse extends ErrorResponse{

    private Map<String , String> fieldErrors;
}
