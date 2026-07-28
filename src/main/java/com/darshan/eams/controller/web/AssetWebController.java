package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.AssetRequestDto;
import com.darshan.eams.dto.response.AssetResponseDto;
import com.darshan.eams.enums.AssetStatus;
import com.darshan.eams.service.interfaces.AssetAllocationService;
import com.darshan.eams.service.interfaces.AssetCategoryService;
import com.darshan.eams.service.interfaces.AssetService;
import com.darshan.eams.service.interfaces.AssetTransferService;
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
@RequestMapping("/assets")
public class AssetWebController {

    private final AssetService assetService;

    private final AssetCategoryService categoryService;

    private final VendorService vendorService;

    private final AssetAllocationService allocationService;

    private final MaintenanceService maintenanceService;

    private final AssetTransferService transferService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) AssetStatus status,
                       @RequestParam(required = false) Long vendorId,
                       @RequestParam(required = false) String sort,
                       Model model) {
        Pageable pageable = PageRequest.of(page, 10, resolveSort(sort, "assetName"));
        Page<AssetResponseDto> result = assetService.filter(keyword, categoryId, status, vendorId, pageable);

        model.addAttribute("assets", result);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("vendorId", vendorId);
        model.addAttribute("currentSort", sort);
        model.addAttribute("categories", categoryService.getAllList());
        model.addAttribute("vendors", vendorService.getAll(PageRequest.of(0, 1000, Sort.by("vendorName"))).getContent());
        model.addAttribute("statuses", AssetStatus.values());
        return "assets/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("assetRequestDto", new AssetRequestDto());
        addDropdowns(model);
        model.addAttribute("isEdit", false);
        return "assets/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AssetResponseDto asset = assetService.getById(id);
        AssetRequestDto dto = new AssetRequestDto();
        dto.setAssetCode(asset.getAssetCode());
        dto.setAssetName(asset.getAssetName());
        dto.setDescription(asset.getDescription());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setPurchaseCost(asset.getPurchaseCost());
        dto.setWarrantyExpiryDate(asset.getWarrantyExpiryDate());
        dto.setLocation(asset.getLocation());
        dto.setCategoryId(asset.getCategoryId());
        dto.setVendorId(asset.getVendorId());

        model.addAttribute("assetRequestDto", dto);
        addDropdowns(model);
        model.addAttribute("assetId", id);
        model.addAttribute("isEdit", true);
        return "assets/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("asset", assetService.getById(id));
        model.addAttribute("allocations", allocationService.getByAsset(id, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "allocationDate"))));
        model.addAttribute("maintenanceRecords", maintenanceService.getByAsset(id, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "reportedDate"))));
        model.addAttribute("transfers", transferService.getHistoryByAsset(id, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "transferDate"))));
        return "assets/detail";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("assetRequestDto") AssetRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDropdowns(model);
            model.addAttribute("isEdit", false);
            return "assets/form";
        }
        assetService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Asset created successfully");
        return "redirect:/assets";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("assetRequestDto") AssetRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addDropdowns(model);
            model.addAttribute("isEdit", true);
            model.addAttribute("assetId", id);
            return "assets/form";
        }
        assetService.update(id, requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Asset updated successfully");
        return "redirect:/assets";
    }

    @PostMapping("/{id}/retire")
    public String retire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        assetService.retire(id);
        redirectAttributes.addFlashAttribute("successMessage", "Asset retired successfully");
        return "redirect:/assets/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        assetService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Asset deleted successfully");
        return "redirect:/assets";
    }

    private void addDropdowns(Model model) {
        model.addAttribute("categories", categoryService.getAllList());
        model.addAttribute("vendors", vendorService.getAll(PageRequest.of(0, 1000, Sort.by("vendorName"))).getContent());
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