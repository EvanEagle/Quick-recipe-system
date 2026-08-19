package com.example.quick_recipe_system.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.quick_recipe_system.entity.Recipe;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiyService {

    private final RecipeService recipeService;
    private final RecipeManagementService recipeManagementService;

    /**
     * 新增 DIY 食譜
     */
    public void createDiyRecipe(
            Recipe recipe,
            String typeString,
            MultipartFile imageFile,
            String username) {

        recipe.setAuthor(username);
        recipe.setSystemRecipe(false);

        recipeManagementService.createRecipe(
                recipe,
                typeString,
                imageFile);
    }

    /**
     * 取得 DIY 食譜
     */
    public Recipe getDiyRecipeForEdit(Long id, String username) {

        Recipe recipe = recipeService.findById(id);

        validateOwner(recipe, username);

        return recipe;
    }

    /**
     * 修改 DIY 食譜
     */
    public void updateDiyRecipe(Long id, Recipe updatedRecipe,
            String username, MultipartFile imageFile) {

        Recipe targetRecipe = recipeService.findById(id);

        validateOwner(targetRecipe, username);

        recipeManagementService.updateRecipe(targetRecipe, updatedRecipe, imageFile);
    }

    /**
     * 刪除 DIY 食譜
     */
    public void deleteDiyRecipe(
            Long id,
            String username) {

        Recipe targetRecipe = recipeService.findById(id);

        validateOwner(targetRecipe, username);

        recipeManagementService.deleteRecipe(targetRecipe);
    }

    /**
     * DIY 食譜權限驗證
     */
    private void validateOwner(
            Recipe recipe,
            String username) {

        if (recipe == null || !Objects.equals(username, recipe.getAuthor()) || recipe.isSystemRecipe()) {

            throw new SecurityException("您沒有權限操作此食譜！");
        }
    }

}
