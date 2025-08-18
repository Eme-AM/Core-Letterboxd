package com.uade.tpo.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @GetMapping("/")
    public String dashboard() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String eventDashboard() {
        return "dashboard";
    }
}
