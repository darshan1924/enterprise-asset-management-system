package com.darshan.eams.controller.web;

import com.darshan.eams.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardWebController {

    private final DashboardService dashboardService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("stats", dashboardService.getDashboardStats());

        // This line was missing - This is causing the error
        model.addAttribute("activePage", "dashboard");

        return "dashboard";
    }
}