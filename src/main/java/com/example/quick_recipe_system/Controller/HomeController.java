package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RecipeService recipeService;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // 1. 從 Session 安全撈取目前登入的使用者帳號
        String currentUsername = (String) session.getAttribute("loggedInUser");

        if (currentUsername != null) {

            // 2. (右側的使用者專屬DIY小面板)
            List<Recipe> diyRecipes = recipeService.getTopLatestDiyRecipes(currentUsername);

            // 3. 投遞給前端畫面
            model.addAttribute("diyRecipes", diyRecipes);
        }
        //不管有沒有登入,都會顯示左側隨機資料
        List<Recipe> randomRecipes = recipeService.getRandomRecipesForHome();

        model.addAttribute("randomRecipes", randomRecipes);

        return "home";
    }
}