package com.darshan.eams.service.interfaces;

import com.darshan.eams.dto.request.DepartmentRequestDto;
import com.darshan.eams.dto.response.DepartmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    DepartmentResponseDto create(DepartmentRequestDto requestDto);

    DepartmentResponseDto update(Long id, DepartmentRequestDto requestDto);

    DepartmentResponseDto getById(Long id);

    Page<DepartmentResponseDto> getAll(Pageable pageable);

    Page<DepartmentResponseDto> search(String keyword, Pageable pageable);

    void delete(Long id);

    long countActiveDepartments();
}