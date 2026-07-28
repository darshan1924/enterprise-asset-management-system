package com.darshan.eams.controller.web;

import com.darshan.eams.dto.response.AuditLogResponseDto;
import com.darshan.eams.service.interfaces.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/audit")
public class AuditWebController {

    private final AuditService auditService;

    @GetMapping
    private String list(@RequestParam(defaultValue = "0") int page, Model model){
        Page<AuditLogResponseDto> logs = auditService.getAllLogs(PageRequest.of(page, 20));
        model.addAttribute("logs", logs);
        return "audit/list";
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    public String entityHistory(@PathVariable String entityName, @PathVariable Long entityId,
                                @RequestParam(defaultValue = "0") int page, Model model) {

        Page<AuditLogResponseDto> logs = auditService.getEntityHistory(entityName, entityId, PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("logs", logs);
        model.addAttribute("entityName", entityName);
        model.addAttribute("entityId", entityId);
        return "audit/entity-history";
    }
}
