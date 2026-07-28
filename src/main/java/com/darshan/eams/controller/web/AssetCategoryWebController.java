package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.AssetCategoryRequestDto;
import com.darshan.eams.dto.response.AssetCategoryResponseDto;
import com.darshan.eams.service.interfaces.AssetCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class AssetCategoryWebController {

    private final AssetCategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<AssetCategoryResponseDto> result =
                categoryService.getAll(PageRequest.of(page, 10, Sort.by("categoryName")));
        model.addAttribute("categories", result);
        return "categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("assetCategoryRequestDto", new AssetCategoryRequestDto());
        model.addAttribute("isEdit", false);
        return "categories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AssetCategoryResponseDto category = categoryService.getById(id);
        AssetCategoryRequestDto dto = new AssetCategoryRequestDto();
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());

        model.addAttribute("assetCategoryRequestDto", dto);
        model.addAttribute("categoryId", id);
        model.addAttribute("isEdit", true);
        return "categories/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("assetCategoryRequestDto") AssetCategoryRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "categories/form";
        }
        categoryService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Category created successfully");
        return "redirect:/categories";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("assetCategoryRequestDto") AssetCategoryRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("categoryId", id);
            return "categories/form";
        }
        categoryService.update(id, requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully");
        return "redirect:/categories";
    }
}