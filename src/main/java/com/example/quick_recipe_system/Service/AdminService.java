package com.example.quick_recipe_system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.repository.FavoriteRepository;
import com.example.quick_recipe_system.repository.RecipeRepository;
import com.example.quick_recipe_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true) // 唯讀事務，能優化資料庫查詢效能
@RequiredArgsConstructor
public class AdminService {

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;
    private final FavoriteRepository favoriteRepository;

    /**
     * 彙整儀表板所需的所有營運數據
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 獲取總會員數
        long totalUsers = userRepository.count();

        // 2. 獲取總食譜數
        long totalRecipes = recipeRepository.count();

        // 3. 計算今日新增食譜數 (商業邏輯封裝在 Service)
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long newRecipesToday = recipeRepository.countByCreatedAtAfter(startOfToday);

        // 將所有數據打包
        stats.put("totalUsers", totalUsers);
        stats.put("totalRecipes", totalRecipes);
        stats.put("newRecipesToday", newRecipesToday);

        return stats;
    }

    // 會員管理-所有一般使用者
    public List<User> getAllMembers() {
        return userRepository.findByRole("ROLE_USER");
    }

    @Transactional
    public void toggleMemberStatus(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到此會員！"));

        // 狀態反轉：如果是 true 就變 false，反之亦然
        user.setIsActive(!user.getIsActive());

        userRepository.save(user);
    }

    /**
     * 食譜管理: 取得所有食譜
     */
    public List<Recipe> getAllRccipes() {
        return recipeRepository.findAllByOrderByIdDesc();
    }

    /**
     * 新增官方食譜
     */
    @Transactional
    public void addOfficialRecipe(Recipe recipe, String typeString, MultipartFile imageFile, String authorName) {

        // 食譜寫入作者名稱(官方帳號)並標記為官方食譜
        recipe.setAuthor(authorName);
        recipe.setIsSystemRecipe(true);

        // 圖片驗證與上傳
        fileStorageService.validateImage(imageFile);

        String imageUrl = fileStorageService.saveUploadedImage(imageFile);
        if (imageUrl != null) {
            recipe.setImageUrl(imageUrl);
        } else {
            recipe.setImageUrl("/images/Notuploaded.jpg");
        }

        if (typeString == null || typeString.isEmpty()) {
            throw new IllegalArgumentException("請選擇食譜分類！");
        }
        recipe.setTypeString(typeString);

        recipeRepository.save(recipe);
    }

    /**
     * 官方強制下架食譜
     */
    @Transactional
    public void officialDeleteRecipe(Long id, String username) {
        Recipe targetRecipe = recipeService.findById(id);

        if (targetRecipe != null) { 
            favoriteRepository.deleteByRecipeId(id);
            fileStorageService.deleteOldImage(targetRecipe.getImageUrl());
            recipeRepository.delete(targetRecipe);
        } else {
            throw new SecurityException("該食譜不存在！");
        }
    }
}
