package com.darshan.eams.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDto {

    private Long id;

    private String departmentName;

    private String departmentCode;

    private String description;

    private String location;

    private long employeeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}