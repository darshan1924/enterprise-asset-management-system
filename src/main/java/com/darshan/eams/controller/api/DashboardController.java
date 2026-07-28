package com.darshan.eams.controller.api;

import com.darshan.eams.dto.common.ApiResponse;
import com.darshan.eams.dto.response.DashboardStatsDto;
import com.darshan.eams.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsDto> getStats() {
        return ApiResponse.success(dashboardService.getDashboardStats());
    }
}