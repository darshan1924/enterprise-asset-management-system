package com.darshan.eams.controller.web;

import com.darshan.eams.dto.request.UserRequestDto;
import com.darshan.eams.dto.response.UserResponseDto;
import com.darshan.eams.enums.UserRole;
import com.darshan.eams.service.interfaces.UserManagementService;
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
@RequestMapping("/users")
public class UserWebController {

    private final UserManagementService userManagementService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<UserResponseDto> result = userManagementService.getAll(PageRequest.of(page, 10, Sort.by("username")));
        model.addAttribute("users", result);
        return "users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userRequestDto", new UserRequestDto());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("isEdit", false);
        return "users/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserResponseDto user = userManagementService.getById(id);
        UserRequestDto dto = new UserRequestDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRoleName());
        dto.setEnabled(user.isEnabled());

        model.addAttribute("userRequestDto", dto);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("userId", id);
        model.addAttribute("isEdit", true);
        return "users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("userRequestDto") UserRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("isEdit", false);
            return "users/form";
        }
        userManagementService.create(requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully");
        return "redirect:/users";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("userRequestDto") UserRequestDto requestDto,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id);
            return "users/form";
        }
        userManagementService.update(id, requestDto);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        return "redirect:/users";
    }

    @PostMapping("/{id}/toggle-enabled")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userManagementService.toggleEnabled(id);
        redirectAttributes.addFlashAttribute("successMessage", "User status updated");
        return "redirect:/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userManagementService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        return "redirect:/users";
    }
}