package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.EmployeeRequestDto;
import com.darshan.eams.dto.response.EmployeeResponseDto;
import com.darshan.eams.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequestDto dto) {
        if (dto == null)
            return null;

        Employee employee = new Employee();
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setDesignation(dto.getDesignation());
        return employee;
    }

    public void updateEntity(Employee employee, EmployeeRequestDto dto) {
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setDesignation(dto.getDesignation());
        }

    public EmployeeResponseDto toResponseDto(Employee employee) {
        return toResponseDto(employee, 0L);
    }

    public EmployeeResponseDto toResponseDto(Employee employee, long currentAllocatedAssetsCount) {
        if (employee == null)
            return null;

        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .designation(employee.getDesignation())
                .status(employee.getStatus())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
                .currentAllocatedAssetsCount(currentAllocatedAssetsCount)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    public List<EmployeeResponseDto> toResponseDtoList(List<Employee> employees) {
        return employees.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}