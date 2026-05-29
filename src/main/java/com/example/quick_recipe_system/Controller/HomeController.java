package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private RecipeService recipeService; // 嚴格遵守架構，只注入 Service

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // 1. 從 Session 安全撈取目前登入的使用者帳號
        String currentUsername = (String) session.getAttribute("loggedInUser"); 
        
        // 2. 嚴格透過 Service 層拿資料，絕不越權呼叫 Repository
        List<Recipe> diyRecipes = recipeService.getTopLatestDiyRecipes(currentUsername);
        
        // 3. 投遞給前端畫面
        model.addAttribute("diyRecipes", diyRecipes);
        
        return "home";
    }
}