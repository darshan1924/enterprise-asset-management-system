package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.DepartmentRequestDto;
import com.darshan.eams.dto.response.DepartmentResponseDto;
import com.darshan.eams.service.interfaces.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DepartmentResponseDto> create(@Valid @RequestBody DepartmentRequestDto requestDto){
        DepartmentResponseDto created = departmentService.create(requestDto);
        return ApiResponse.success("Department created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<DepartmentResponseDto> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequestDto requestDto){
        DepartmentResponseDto updated = departmentService.update(id, requestDto);
        return ApiResponse.success("Department updated successfully", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<DepartmentResponseDto> getById(@PathVariable Long id){
        return ApiResponse.success(departmentService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<DepartmentResponseDto>> getAll(@PageableDefault(size = 10, sort = "departmentName")Pageable pageable){
        Page<DepartmentResponseDto> page = departmentService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<DepartmentResponseDto>> search(@RequestParam String keyword, @PageableDefault(size = 10, sort = "departmentName") Pageable pageable){
        Page<DepartmentResponseDto> page = departmentService.search(keyword, pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id){
        departmentService.delete(id);
        return ApiResponse.success("Department deleted successfully", null);
    }
}
