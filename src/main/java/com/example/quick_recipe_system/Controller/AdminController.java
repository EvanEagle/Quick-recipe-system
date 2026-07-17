package com.example.quick_recipe_system.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.quick_recipe_system.repository.RecipeRepository;
import com.example.quick_recipe_system.repository.UserRepository;
import com.example.quick_recipe_system.service.AdminService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    // admin/dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // 透過 Service 獲取打包好的數據
        Map<String, Object> dashboardData = adminService.getDashboardData();

        // 將數據拆分裝入 Model
        model.addAttribute("totalUsers", dashboardData.get("totalUsers"));
        model.addAttribute("totalRecipes", dashboardData.get("totalRecipes"));
        model.addAttribute("newRecipesToday", dashboardData.get("newRecipesToday"));

        return "admin/dashboard";
    }

    // 會員管理
    @GetMapping("/members")
    public String showMembers(Model model) {
        model.addAttribute("members", adminService.getAllMembers());
        return "admin/members";
    }

    // 食譜管理
    @GetMapping("/recipes")
    public String showRecipes(Model model) {
        model.addAttribute("recipes", recipeRepository.findAll().toString());
        return "admin/recipes";
    }

    // 會員狀態切換
    @PostMapping("/members/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        System.out.println("===> 收到切換會員狀態請求，Target User ID: " + id);
        adminService.toggleMemberStatus(id);
        
        return "redirect:/admin/members";
    }
}
