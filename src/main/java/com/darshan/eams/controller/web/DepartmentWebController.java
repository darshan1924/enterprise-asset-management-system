package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.DepartmentRequestDto;
import com.darshan.eams.dto.response.DepartmentResponseDto;
import com.darshan.eams.service.interfaces.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentWebController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "departmentName"));
        Page<DepartmentResponseDto> result = (keyword != null && !keyword.isBlank())
                ? departmentService.search(keyword, pageable)
                : departmentService.getAll(pageable);

        model.addAttribute("departments", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSort", sort);
        return "departments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("departmentRequestDto", new DepartmentRequestDto());
        model.addAttribute("isEdit", false);
        return "departments/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        DepartmentResponseDto department = departmentService.getById(id);
        DepartmentRequestDto dto = new DepartmentRequestDto();
        dto.setDepartmentName(department.getDepartmentName());
        dto.setDepartmentCode(department.getDepartmentCode());
        dto.setDescription(department.getDescription());
        dto.setLocation(department.getLocation());

        model.addAttribute("departmentRequestDto", dto);
        model.addAttribute("departmentId", id);
        model.addAttribute("isEdit", true);
        return "departments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("departmentRequestDto") DepartmentRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "departments/form";
        }
        departmentService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Department created successfully");
        return "redirect:/departments";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("departmentRequestDto") DepartmentRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("departmentId", id);
            return "departments/form";
        }
        departmentService.update(id, requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully");
        return "redirect:/departments";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        departmentService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully");
        return "redirect:/departments";
    }

    private Sort resolveSort(String sortParam, String defaultField) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(defaultField);
        }
        String[] parts = sortParam.split(",");
        String field = parts[0];
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}