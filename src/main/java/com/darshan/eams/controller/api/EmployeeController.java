package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.EmployeeRequestDto;
import com.darshan.eams.dto.response.EmployeeResponseDto;
import com.darshan.eams.enums.EmployeeStatus;
import com.darshan.eams.service.interfaces.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmployeeResponseDto> create(@Valid @RequestBody EmployeeRequestDto requestDto) {
        EmployeeResponseDto created = employeeService.create(requestDto);
        return ApiResponse.success("Employee created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<EmployeeResponseDto> update(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequestDto requestDto) {
        EmployeeResponseDto updated = employeeService.update(id, requestDto);
        return ApiResponse.success("Employee updated successfully", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<EmployeeResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(employeeService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<EmployeeResponseDto>> getAll(@PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        Page<EmployeeResponseDto> page = employeeService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/department/{departmentId}")
    public ApiResponse<PageResponse<EmployeeResponseDto>> getByDepartment(@PathVariable Long departmentId, @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        Page<EmployeeResponseDto> page = employeeService.getByDepartment(departmentId, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<PageResponse<EmployeeResponseDto>> getByStatus(@PathVariable EmployeeStatus status, @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        Page<EmployeeResponseDto> page = employeeService.getByStatus(status, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<EmployeeResponseDto>> search(@RequestParam String keyword, @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        Page<EmployeeResponseDto> page = employeeService.search(keyword, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ApiResponse.success("Employee deleted successfully", null);
    }
}