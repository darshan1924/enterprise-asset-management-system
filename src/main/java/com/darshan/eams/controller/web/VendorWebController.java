package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.VendorRequestDto;
import com.darshan.eams.dto.response.VendorResponseDto;
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
@RequestMapping("/vendors")
public class VendorWebController {

    private final VendorService vendorService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "vendorName"));
        Page<VendorResponseDto> result = (keyword != null && !keyword.isBlank())
                ? vendorService.search(keyword, pageable)
                : vendorService.getAll(pageable);

        model.addAttribute("vendors", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentSort", sort);
        return "vendors/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("vendorRequestDto", new VendorRequestDto());
        model.addAttribute("isEdit", false);
        return "vendors/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        VendorResponseDto vendor = vendorService.getById(id);
        VendorRequestDto dto = new VendorRequestDto();
        dto.setVendorName(vendor.getVendorName());
        dto.setContactPerson(vendor.getContactPerson());
        dto.setEmail(vendor.getEmail());
        dto.setPhoneNumber(vendor.getPhoneNumber());
        dto.setAddress(vendor.getAddress());
        dto.setRating(vendor.getRating());

        model.addAttribute("vendorRequestDto", dto);
        model.addAttribute("vendorId", id);
        model.addAttribute("isEdit", true);
        return "vendors/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("vendorRequestDto") VendorRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "vendors/form";
        }
        vendorService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Vendor created successfully");
        return "redirect:/vendors";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("vendorRequestDto") VendorRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("vendorId", id);
            return "vendors/form";
        }
        vendorService.update(id, requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Vendor updated successfully");
        return "redirect:/vendors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vendorService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Vendor deleted successfully");
        return "redirect:/vendors";
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