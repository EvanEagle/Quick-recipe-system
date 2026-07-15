package com.example.quick_recipe_system.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quick_recipe_system.service.AdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    //admin/dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        
        // 透過 Service 獲取打包好的數據
        Map<String, Object> dashboardData = adminService.getDashboardData();
        
        // 將數據拆分裝入 Model，這可以確保你原本寫好的 Thymeleaf 變數完全不用修改
        model.addAttribute("totalUsers", dashboardData.get("totalUsers"));
        model.addAttribute("totalRecipes", dashboardData.get("totalRecipes"));
        model.addAttribute("newRecipesToday", dashboardData.get("newRecipesToday"));
        
        return "admin/dashboard"; 
    }

}
