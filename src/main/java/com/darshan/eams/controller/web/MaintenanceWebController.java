package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.MaintenanceRequestDto;
import com.darshan.eams.dto.response.MaintenanceResponseDto;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.service.interfaces.AssetService;
import com.darshan.eams.service.interfaces.MaintenanceService;
import com.darshan.eams.service.interfaces.VendorService;
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
@RequestMapping("/maintenance")
public class MaintenanceWebController {

    private final MaintenanceService maintenanceService;

    private final AssetService assetService;

    private final VendorService vendorService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "reportedDate"));
        Page<MaintenanceResponseDto> result = maintenanceService.getAll(pageable);
        model.addAttribute("maintenanceRecords", result);
        model.addAttribute("currentSort", sort);
        return "maintenance/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("maintenanceRequestDto", new MaintenanceRequestDto());
        addDropdowns(model);
        return "maintenance/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("maintenanceRequestDto") MaintenanceRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDropdowns(model);
            return "maintenance/form";
        }
        maintenanceService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance ticket raised successfully");
        return "redirect:/maintenance";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        maintenanceService.complete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance ticket marked as completed");
        return "redirect:/maintenance";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        maintenanceService.cancel(id);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance ticket cancelled");
        return "redirect:/maintenance";
    }

    private void addDropdowns(Model model) {
        model.addAttribute("assets",
                assetService.filter(null, null, AssetStatus.AVAILABLE, null, PageRequest.of(0, 1000, Sort.by("assetName"))).getContent());
        model.addAttribute("vendors",
                vendorService.getAll(PageRequest.of(0, 1000, Sort.by("vendorName"))).getContent());
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