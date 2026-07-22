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
            recipe.setImageUrl("/images/Notuploaded.jpg"); // 預設圖
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
                            && !existingRecipe.getImageUrl().equals("/images/Notuploaded.jpg")) {
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

    /**
     * --- Service 層 ---
     * 方法名稱：deleteRecipe(傳入變數： 要刪除的 Integer id, 傳入變數：當前登入者 String username)
     * 
     * 1. 尋找目標：
     * 呼叫 findById(id) 找出那道準備被刪除的食譜 (假設命名為 targetRecipe)
     * 
     * 2. 權限防護罩：
     * IF (targetRecipe 確實存在 AND targetRecipe 的作者名稱 和 username 一模一樣) {
     * 
     * // 3. 執行移除動作
     * 呼叫recipeRepository內的delete 直接連同副表裡面的食材、步驟一起完美刪除乾淨
     * 
     * } ELSE {
     * // 4. 越權處理
     * 拋出一個新的例外 (例如 SecurityException)
     * 例外訊息設定為："您沒有權限刪除此食譜！"
     * }
     */
    @Transactional // 加上這個，確保刪除收藏跟刪除食譜同進同退
    public void deleteDiyRecipe(Long id, String username) {

        Recipe targetRecipe = recipeService.findById(id);

        // 1. 資安防護：檢查食譜存不存在，且「現在登入的人」是不是「食譜的作者」
        if (targetRecipe != null && targetRecipe.getAuthor().equals(username)) {

            // 2. 解除外鍵綁定：先無差別刪除這道菜在 Favorite 表裡的所有收藏紀錄
            // (因為這道菜要從世界上消失了，所以不管誰收藏過，都要清掉)
            favoriteRepository.deleteByRecipeId(id);

            // 3. 在食譜從資料庫消失之前，先把硬碟裡的照片刪掉！
            // (不用擔心刪到預設圖片，因為你在 deleteOldImage 裡已經寫好保護機制的 if 判斷了)
            fileStorageService.deleteOldImage(targetRecipe.getImageUrl());
            // 4. 安全刪除：相關的收藏紀錄都清空了，不會再噴 500 錯誤，安心刪除食譜！
            recipeRepository.delete(targetRecipe);

        } else {
            // 防禦水平越權：如果不是作者，直接拋出權限異常！
            throw new SecurityException("您沒有權限刪除此食譜！");
        }
    }

}
