package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.AssetTransferRequestDto;
import com.darshan.eams.dto.response.AssetTransferResponseDto;
import com.darshan.eams.service.interfaces.AssetService;
import com.darshan.eams.service.interfaces.AssetTransferService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/transfers")
public class AssetTransferWebController {

    private final AssetTransferService transferService;

    private final AssetService assetService;

    private final DepartmentService departmentService;

    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "transferDate"));
        Page<AssetTransferResponseDto> result = transferService.getAll(pageable);
        model.addAttribute("transfers", result);
        model.addAttribute("currentSort", sort);
        return "transfers/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("assetTransferRequestDto", new AssetTransferRequestDto());
        addDropdowns(model);
        return "transfers/form";
    }

    @PostMapping
    public String initiate(@Valid @ModelAttribute("assetTransferRequestDto") AssetTransferRequestDto requestDto,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDropdowns(model);
            return "transfers/form";
        }
        transferService.initiate(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Asset transfer initiated successfully");
        return "redirect:/transfers";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transferService.approve(id);
        redirectAttributes.addFlashAttribute("successMessage", "Transfer approved");
        return "redirect:/transfers";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transferService.complete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Transfer completed");
        return "redirect:/transfers";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transferService.reject(id);
        redirectAttributes.addFlashAttribute("successMessage", "Transfer rejected");
        return "redirect:/transfers";
    }

    private void addDropdowns(Model model) {
        model.addAttribute("assets", assetService.getAll(PageRequest.of(0, 1000, Sort.by("assetName"))).getContent());
        model.addAttribute("departments", departmentService.getAll(PageRequest.of(0, 1000, Sort.by("departmentName"))).getContent());
        model.addAttribute("employees", employeeService.getAll(PageRequest.of(0, 1000, Sort.by("firstName"))).getContent());
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