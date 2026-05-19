package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.quick_recipe_system.model.cuisine.CuisineType;
import com.example.quick_recipe_system.service.RecipeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecipeController {


    private final RecipeService recipeService;

    @GetMapping("/recipes")
    public String showRecipeType(Model model) {
        List<CuisineType> types = recipeService.getAllCuisineTypes();

        model.addAttribute("types", types);

        return "recipe-list";
    }
    
}
