package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.model.Recipe;
import com.example.quick_recipe_system.model.cuisine.CuisineType;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe")
    public String showRecipeType(Model model) {
        List<CuisineType> types = recipeService.getAllCuisineTypes();

        model.addAttribute("types", types);

        return "recipe-list";
    }

    @GetMapping("/diy")
    public String showDiyPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }
        List<Recipe> myDiyRecipes = recipeService.findRecipesByAuthor(username);
        model.addAttribute("diyRecipes", myDiyRecipes);
        return "diy-page";
    }


    @GetMapping("/recipe/add")
    public String showAddRecipe(HttpSession session, Model model) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("recipe", new Recipe());
        return "recipe-add";
    }

    @PostMapping("/recipe/add")
    public String addRecipe(HttpSession session, Recipe recipe, String typeString,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }
        try {
            recipe.setAuthor(username);

            recipeService.addRecipe(recipe, typeString);
            redirectAttributes.addFlashAttribute("successMsg", "新增食譜成功！");
            return "redirect:/diy";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "新增失敗：請選擇正確的食譜分類！");
            return "redirect:/recipe/add";
        }
    }

    @GetMapping("recipe/edit/{id}")
    public String showEditRecipeForm(@PathVariable Integer id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        
        String username = (String) session.getAttribute("loggedInUser");
        
        if (username == null) {
            return "redirect:/login";
        }

        Recipe findId = recipeService.findById(id);

        if (findId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "找不到該食譜！");
            return "redirect:/diy";
        }

        if (!username.equals(findId.getAuthor())) {
            redirectAttributes.addFlashAttribute("errorMsg", "您無權修改別人的食譜！");
            return "redirect:/diy";
        }

        model.addAttribute("recipe", findId);
        return "recipe-edit";
    }

    @PostMapping("/recipe/edit/{id}")
    public String updateRecipe(@PathVariable Integer id, Recipe updateRecipe, HttpSession session, RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        recipeService.updateRecipe(id, updateRecipe);

        redirectAttributes.addFlashAttribute("successMsg", "食譜修改成功！");
        return "redirect:/diy";
    }
}