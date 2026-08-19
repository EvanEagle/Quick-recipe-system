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

        String username = (String) session.getAttribute("loggedInUser");
        String role = (String) session.getAttribute("loggedInUserRole");

        if (username != null) {
            model.addAttribute("manageRecipes", recipeService.getHomeManageRecipes(username, role));
        }
        // 不管有沒有登入,都會顯示左側隨機資料
        List<Recipe> randomRecipes = recipeService.getRandomRecipesForHome();

        model.addAttribute("randomRecipes", randomRecipes);

        return "home";
    }
}