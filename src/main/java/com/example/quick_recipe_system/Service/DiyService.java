package com.example.quick_recipe_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.repository.FavoriteRepository;
import com.example.quick_recipe_system.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiyService {

    private final RecipeService recipeService;
    private final FileStorageService fileStorageService;
    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;

    /**
     * 新增食譜
     */
    public void createDiyRecipe(Recipe recipe, String typeString, MultipartFile imageFile, String username) {

        // 1. 設定作者與食譜類型 (確保為 DIY)
        recipe.setAuthor(username);
        recipe.setIsSystemRecipe(false);

        // 2. 處理圖片驗證與上傳
        fileStorageService.validateImage(imageFile);

        // 呼叫輔助方法存圖片
        String imageUrl = fileStorageService.saveUploadedImage(imageFile);
        if (imageUrl != null) {
            recipe.setImageUrl(imageUrl);
        } else {
            recipe.setImageUrl("/images/system/not-uploaded.jpg"); // 預設圖
        }
        if (typeString == null || typeString.isEmpty()) {
            throw new IllegalArgumentException("新增失敗：請選擇正確的食譜分類！");
        }
        recipe.setTypeString(typeString);

        recipeRepository.save(recipe);
    }

    /**
     * 更新食譜
     */
    @Transactional
    public void updateDiyRecipe(Recipe updateRecipe, String username, MultipartFile imageFile) {

        Recipe existingRecipe = recipeService.findById(updateRecipe.getId());

        if (existingRecipe != null && existingRecipe.getAuthor().equals(username)) {

            existingRecipe.setName(updateRecipe.getName());
            existingRecipe.setCookingTime(updateRecipe.getCookingTime());
            existingRecipe.setIngredients(updateRecipe.getIngredients());
            existingRecipe.setSeasonings(updateRecipe.getSeasonings());
            existingRecipe.setSteps(updateRecipe.getSteps());
            existingRecipe.setKeywords(updateRecipe.getKeywords());

            if (imageFile != null && !imageFile.isEmpty()) {
                fileStorageService.validateImage(imageFile);
                String newImageUrl = fileStorageService.saveUploadedImage(imageFile);

                if (newImageUrl != null) {
                    if (existingRecipe.getImageUrl() != null
                            && !existingRecipe.getImageUrl().equals("/images/system/not-uploaded.jpg")) {
                        fileStorageService.deleteOldImage(existingRecipe.getImageUrl());
                    }
                    existingRecipe.setImageUrl(newImageUrl);
                }
            }
            recipeRepository.save(existingRecipe);
        } else {
            throw new SecurityException("您沒有權限修改此食譜或食譜不存在！");
        }
    }

    
    @Transactional
    public void deleteDiyRecipe(Long id, String username) {

        Recipe targetRecipe = recipeService.findById(id);

        // 1. 檢查食譜存在，而且登入者是作者
        if (targetRecipe != null && targetRecipe.getAuthor().equals(username)) {

            // 2. 先記住圖片網址
            String imageUrl = targetRecipe.getImageUrl();

            // 3. 先刪除收藏資料，解除外鍵關聯
            favoriteRepository.deleteByRecipeId(id);

            // 4. 刪除食譜資料
            recipeRepository.delete(targetRecipe);

            // 5. 再刪除實體圖片
            fileStorageService.deleteOldImage(imageUrl);

        } else {
            throw new SecurityException("您沒有權限刪除此食譜！");
        }
    }
}
