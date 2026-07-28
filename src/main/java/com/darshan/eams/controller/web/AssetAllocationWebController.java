package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.AssetAllocationRequestDto;
import com.darshan.eams.dto.response.AssetAllocationResponseDto;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.service.interfaces.AssetAllocationService;
import com.darshan.eams.service.interfaces.AssetService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/allocations")
public class AssetAllocationWebController {

    private final AssetAllocationService allocationService;

    private final AssetService assetService;

    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "allocationDate"));
        Page<AssetAllocationResponseDto> result = allocationService.getAll(pageable);
        model.addAttribute("allocations", result);
        model.addAttribute("currentSort", sort);
        return "allocations/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("assetAllocationRequestDto", new AssetAllocationRequestDto());
        model.addAttribute("availableAssets",
                assetService.filter(null, null, AssetStatus.AVAILABLE, null, PageRequest.of(0, 1000, Sort.by("assetName"))).getContent());
        model.addAttribute("employees",
                employeeService.getAll(PageRequest.of(0, 1000, Sort.by("firstName"))).getContent());
        return "allocations/form";
    }

    @PostMapping
    public String allocate(@Valid @ModelAttribute("assetAllocationRequestDto") AssetAllocationRequestDto requestDto,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("availableAssets",
                    assetService.filter(null, null, AssetStatus.AVAILABLE, null, PageRequest.of(0, 1000, Sort.by("assetName"))).getContent());
            model.addAttribute("employees",
                    employeeService.getAll(PageRequest.of(0, 1000, Sort.by("firstName"))).getContent());
            return "allocations/form";
        }
        allocationService.allocate(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Asset allocated successfully");
        return "redirect:/allocations";
    }

    @PostMapping("/{id}/return")
    public String returnAsset(@PathVariable Long id, @RequestParam(required = false) String remarks,
                              RedirectAttributes redirectAttributes) {
        allocationService.returnAsset(id, remarks);
        redirectAttributes.addFlashAttribute("successMessage", "Asset returned successfully");
        return "redirect:/allocations";
    }

    private Sort resolveSort(String sortParam, String defaultField) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, defaultField);
        }
        String[] parts = sortParam.split(",");
        String field = parts[0];
        Sort.Direction direction = (parts.length > 1 && parts[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}