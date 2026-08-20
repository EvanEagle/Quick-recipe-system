package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    /**
     * 根據 ID 尋找特定的食譜
     */
    public Recipe findById(Long id) {

        return recipeRepository.findById(id).orElse(null);
    }

    /**
     * 根據作者名稱尋找所有食譜
     */
    public List<Recipe> findRecipesByAuthor(String username) {

        return recipeRepository.findByAuthor(username);
    }

    /**
     * 探索食譜-按照料理類型分類好撈出的所有食譜
     */
    public Map<String, List<Recipe>> getAllRecipes() {

        // 使用LinkedHashMap讓食譜按照我排列的順序顯示
        Map<String, List<Recipe>> resultMap = new LinkedHashMap<>();

        // 希望撈出的食譜按照類型排列好,所以呼叫Repository.findByTypeString 這個方法
        resultMap.put("中式", recipeRepository.findByTypeString("Chinese"));
        resultMap.put("日式", recipeRepository.findByTypeString("Japanese"));
        resultMap.put("西式", recipeRepository.findByTypeString("Western"));

        return resultMap;
    }

    /**
     * 解析首頁的烹調時間字串，並呼叫對應的 JPA 查詢
     * 利用 Java 現代的 Switch 表達式與 Map.of 一步到位
     */
    public Map<String, List<Recipe>> searchByCookingTimeStr(String timeStr) {

        return switch (timeStr) {
            case "10mins" ->
                Map.of("⏱️ 10分鐘快手", recipeRepository.findByCookingTimeLessThanEqualOrderByCookingTimeAsc(10));
            case "20mins" ->
                Map.of("⏱️ 20分鐘輕鬆上菜", recipeRepository.findByCookingTimeLessThanEqualOrderByCookingTimeAsc(20));
            case "30mins" ->
                Map.of("⏱️ 30分鐘經典", recipeRepository.findByCookingTimeLessThanEqualOrderByCookingTimeAsc(30));
            case "30mins up" ->
                Map.of("🍳 30分鐘以上功夫菜", recipeRepository.findByCookingTimeGreaterThanOrderByCookingTimeAsc(30));

            // 預設 fallback：當網址亂打或沒有匹配時
            default -> getAllRecipes();
        };
    }

    /**
     * 搜尋食譜的共同邏輯
     */
    public Map<String, List<Recipe>> masterSearch(String keyword, String cookingtime, String typeString,
            String source) {

        Map<String, List<Recipe>> resultMap = new LinkedHashMap<>();

        // 優先權 1：關鍵字綜合搜尋 (大搜尋框：菜名、食材、標籤)
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Recipe> recipes = recipeRepository.searchByComprehensiveKeyword(keyword);
            resultMap.put("🔍 搜尋結果：'" + keyword + "'", recipes);
            return resultMap;
        }

        // 優先權 2：烹調時間搜尋
        if (cookingtime != null && !cookingtime.isEmpty()) {
            return searchByCookingTimeStr(cookingtime);
        }

        // 優先權 3：料理類型搜尋 (中式 / 日式 / 西式 按鈕)
        if (typeString != null && !typeString.isEmpty()) {
            List<Recipe> recipes = recipeRepository.findByTypeString(typeString);
            resultMap.put(typeString, recipes);
            return resultMap;
        }

        // 優先權 4：食譜來源搜尋
        // 官方食譜應依 isSystemRecipe 判斷，不綁定特定管理員帳號。
        if ("official".equals(source)) {
            List<Recipe> recipes = recipeRepository.findBySystemRecipeTrueOrderByIdDesc();
            resultMap.put("官方食譜", recipes);
            return resultMap;
        }

        // 預設：什麼都沒選，顯示探索食譜畫面
        return getAllRecipes();
    }

    /**
     * 首頁右側的DIY食譜列表(分類系統管理員或會員)
     */
    public List<Recipe> getHomeManageRecipes(String username, String role) {

        if ("ROLE_ADMIN".equals(role)) {
            return recipeRepository.findTop5BySystemRecipeTrueOrderByIdDesc();
        }

        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return recipeRepository.findTop5ByAuthorOrderByIdDesc(username);
    }

    /**
     * 首頁左側的隨機食譜列表
     */
    public List<Recipe> getRandomRecipesForHome() {
        return recipeRepository.findRandom4Recipes();
    }
}
