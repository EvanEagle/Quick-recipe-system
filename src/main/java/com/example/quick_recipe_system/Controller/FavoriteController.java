package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.quick_recipe_system.model.Recipe;
import com.example.quick_recipe_system.service.FavoriteService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/favorite")
    public String showFavoritesPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        List<Recipe> myFavorites = favoriteService.getFavoriteRecipe(username);
        model.addAttribute("favorites", myFavorites);

        return "favorite";
    }
}
