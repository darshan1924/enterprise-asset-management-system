package com.darshan.eams.controller.web;

import com.darshan.eams.entity.User;
import com.darshan.eams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileWebController {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile/change-password")
    public String changePasswordForm() {
        return "auth/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes){
        User user = userRepository.findByUsernameWithRole(authentication.getName())
                .orElseThrow(()-> new IllegalStateException("Authenticated user not found in database"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())){
            model.addAttribute("errorMessage", "Current password is incorrect");
            return "auth/change-password";
        }

        if (newPassword.length()<8){
            model.addAttribute("errorMessage", "New password must be at least 8 characters");
            return "auth/change-password";
        }

        if (!newPassword.equals(confirmPassword)){
            model.addAttribute("errorMessage", "New password and confirm password do not match");
            return "auth/change-password";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("successMessage", "Password changes successfully");
        return "redirect:/dashboard";
    }
}
