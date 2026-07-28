package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.common.PageResponse;
import com.darshan.eams.dto.request.AssetCategoryRequestDto;
import com.darshan.eams.dto.response.AssetCategoryResponseDto;
import com.darshan.eams.service.interfaces.AssetCategoryService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AssetCategoryResponseDto> create(@Valid @RequestBody AssetCategoryRequestDto requestDto) {
        AssetCategoryResponseDto created = categoryService.create(requestDto);
        return ApiResponse.success("Category created successfully", created);
    }

    @PutMapping("/{id}")
    public ApiResponse<AssetCategoryResponseDto> update(@PathVariable Long id, @Valid @RequestBody AssetCategoryRequestDto requestDto) {
        AssetCategoryResponseDto updated = categoryService.update(id, requestDto);
        return ApiResponse.success("Category updated successfully", updated);
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetCategoryResponseDto> getById(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getById(id));
    }

    @GetMapping
    public ApiResponse<PageResponse<AssetCategoryResponseDto>> getAll(@PageableDefault(size = 10, sort = "categoryName") Pageable pageable) {
        Page<AssetCategoryResponseDto> page = categoryService.getAll(pageable);
        return ApiResponse.success(PageResponse.from(page));
    }

    @GetMapping("/list")
    public ApiResponse<List<AssetCategoryResponseDto>> getAllList() {
        return ApiResponse.success(categoryService.getAllList());
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ApiResponse.success("Category deleted successfully", null);
    }
}