package com.example.quick_recipe_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.repository.FavoriteRepository;
import com.example.quick_recipe_system.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 因DiyService 和 AdminService 都有同樣的方法，所以抽離成 RecipeManagementService
 */
@Service
@RequiredArgsConstructor
public class RecipeManagementService {

    private static final String DEFAULT_IMAGE = "/images/system/not-uploaded.jpg";

    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;
    private final FileStorageService fileStorageService;

    /**
     * 共用：新增食譜
     */
    public void createRecipe(
            Recipe recipe,
            String typeString,
            MultipartFile imageFile) {

        if (typeString == null || typeString.isBlank()) {
            throw new IllegalArgumentException("請選擇食譜分類！");
        }

        recipe.setTypeString(typeString);
        recipe.setImageUrl(saveImageOrDefault(imageFile));

        recipeRepository.save(recipe);
    }

    /**
     * 共用：新增時處理圖片
     */
    private String saveImageOrDefault(MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()) {
            return DEFAULT_IMAGE;
        }

        fileStorageService.validateImage(imageFile);

        String imageUrl = fileStorageService.saveUploadedImage(imageFile);

        return imageUrl != null ? imageUrl : DEFAULT_IMAGE;
    }

    /**
     * 共用：修改食譜
     */
    @Transactional
    public void updateRecipe(
            Recipe targetRecipe,
            Recipe updatedRecipe,
            MultipartFile imageFile) {

        updateFields(targetRecipe, updatedRecipe);
        updateImage(targetRecipe, imageFile);

        recipeRepository.save(targetRecipe);
    }

    /**
     * 共用：更新一般欄位
     */
    private void updateFields(
            Recipe targetRecipe,
            Recipe updatedRecipe) {

        targetRecipe.setName(updatedRecipe.getName());
        targetRecipe.setCookingTime(updatedRecipe.getCookingTime());
        targetRecipe.setIngredients(updatedRecipe.getIngredients());
        targetRecipe.setSeasonings(updatedRecipe.getSeasonings());
        targetRecipe.setSteps(updatedRecipe.getSteps());
        targetRecipe.setKeywords(updatedRecipe.getKeywords());

        if (updatedRecipe.getTypeString() != null && !updatedRecipe.getTypeString().isBlank()) {
            targetRecipe.setTypeString(updatedRecipe.getTypeString());
        }
    }

    /**
     * 共用：修改時處理圖片
     */
    private void updateImage(
            Recipe targetRecipe,
            MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()) {
            return;
        }

        fileStorageService.validateImage(imageFile);

        String newImageUrl = fileStorageService.saveUploadedImage(imageFile);

        if (newImageUrl == null) {
            return;
        }

        String oldImageUrl = targetRecipe.getImageUrl();

        if (oldImageUrl != null
                && !DEFAULT_IMAGE.equals(oldImageUrl)) {

            fileStorageService.deleteOldImage(oldImageUrl);
        }

        targetRecipe.setImageUrl(newImageUrl);
    }

    /**
     * 共用：刪除食譜
     */
    @Transactional
    public void deleteRecipe(Recipe targetRecipe) {

        String imageUrl = targetRecipe.getImageUrl();

        favoriteRepository.deleteByRecipeId(targetRecipe.getId());

        recipeRepository.delete(targetRecipe);

        fileStorageService.deleteOldImage(imageUrl);
    }

}