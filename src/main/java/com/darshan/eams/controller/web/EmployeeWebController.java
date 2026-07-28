package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.EmployeeRequestDto;
import com.darshan.eams.dto.response.DepartmentResponseDto;
import com.darshan.eams.dto.response.EmployeeResponseDto;
import com.darshan.eams.service.interfaces.DepartmentService;
import com.darshan.eams.service.interfaces.EmployeeService;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeWebController {

    private final EmployeeService employeeService;

    private final DepartmentService departmentService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "firstName"));
        Page<EmployeeResponseDto> result = (keyword != null && !keyword.isBlank())
                ? employeeService.search(keyword, pageable)
                : employeeService.getAll(pageable);

        model.addAttribute("employees", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSort", sort);
        return "employees/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("employeeRequestDto", new EmployeeRequestDto());
        model.addAttribute("departments", allDepartments());
        model.addAttribute("isEdit", false);
        return "employees/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        EmployeeResponseDto employee = employeeService.getById(id);
        EmployeeRequestDto dto = new EmployeeRequestDto();
        dto.setEmployeeCode(employee.getEmployeeCode());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhoneNumber(employee.getPhoneNumber());
        dto.setDesignation(employee.getDesignation());
        dto.setDepartmentId(employee.getDepartmentId());

        model.addAttribute("employeeRequestDto", dto);
        model.addAttribute("departments", allDepartments());
        model.addAttribute("employeeId", id);
        model.addAttribute("isEdit", true);
        return "employees/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("employeeRequestDto") EmployeeRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", allDepartments());
            model.addAttribute("isEdit", false);
            return "employees/form";
        }
        employeeService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully");
        return "redirect:/employees";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("employeeRequestDto") EmployeeRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", allDepartments());
            model.addAttribute("isEdit", true);
            model.addAttribute("employeeId", id);
            return "employees/form";
        }
        employeeService.update(id, requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully");
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully");
        return "redirect:/employees";
    }

    private List<DepartmentResponseDto> allDepartments() {
        return departmentService.getAll(PageRequest.of(0, 1000, Sort.by("departmentName"))).getContent();
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