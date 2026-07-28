package com.darshan.eams.mapper;

import com.darshan.eams.dto.request.DepartmentRequestDto;
import com.darshan.eams.dto.response.DepartmentResponseDto;
import com.darshan.eams.entity.Department;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequestDto dto){
        if (dto == null)
            return null;

        Department department = new Department();
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentCode(dto.getDepartmentCode());
        department.setDescription(dto.getDescription());
        department.setLocation(dto.getLocation());
        return department;
    }

    public void updateEntity(Department department, DepartmentRequestDto dto){
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentCode(dto.getDepartmentCode());
        department.setDescription(dto.getDescription());
        department.setLocation(dto.getLocation());
    }

    public DepartmentResponseDto toResponseDto(Department department) {
        return toResponseDto(department, 0L);
    }

    public DepartmentResponseDto toResponseDto(Department department, long employeeCount) {
        if (department == null)
            return null;

        return DepartmentResponseDto.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .departmentCode(department.getDepartmentCode())
                .description(department.getDescription())
                .location(department.getLocation())
                .employeeCount(employeeCount)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    public List<DepartmentResponseDto> toResponseDtoList(List<Department> departments) {
        return departments.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}
