package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.EmployeeRequestDto;
import com.darshan.eams.dto.response.EmployeeResponseDto;
import com.darshan.eams.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponseDto create(EmployeeRequestDto requestDto);

    EmployeeResponseDto update(Long id, EmployeeRequestDto requestDto);

    EmployeeResponseDto getById(Long id);

    Page<EmployeeResponseDto> getAll(Pageable pageable);

    Page<EmployeeResponseDto> getByDepartment(Long departmentId, Pageable pageable);

    Page<EmployeeResponseDto> getByStatus(EmployeeStatus status, Pageable pageable);

    Page<EmployeeResponseDto> search(String keyword, Pageable pageable);

    void delete(Long id);

    long countActiveEmployees();
}