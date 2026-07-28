package com.darshan.eams.dto.response;

import com.darshan.eams.enums.EmployeeStatus;
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
public class EmployeeResponseDto {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String designation;

    private EmployeeStatus status;

    private Long departmentId;

    private String departmentName;

    private long currentAllocatedAssetsCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}