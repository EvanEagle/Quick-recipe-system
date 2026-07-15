package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.repository.FavoriteRepository;
import com.example.quick_recipe_system.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final FavoriteRepository favoriteRepository;
    private final FileStorageService fileStorageService;

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
     * 根據 ID 尋找特定的食譜(修改及刪除食譜需要用到)
     */
    public Recipe findById(Integer id) {

        return recipeRepository.findById(id).orElse(null);
    }

    public List<Recipe> findRecipesByAuthor(String username) {

        return recipeRepository.findByAuthor(username);
    }

    /**
     * 新增食譜
     */
    public void addRecipe(Recipe recipe, String typeString) {
        recipe.setTypeString(typeString);
        recipeRepository.save(recipe);
    }

    /**
     * 更新食譜
     */
    public void updateRecipe(Recipe updateRecipe, String username) {

        Recipe existingRecipe = findById(updateRecipe.getId());

        if (existingRecipe != null && existingRecipe.getAuthor().equals(username)) {

            updateRecipe.setAuthor(username); // 將食譜的作者設定為原本的作者
            updateRecipe.setTypeString(existingRecipe.getTypeString()); // 料理類型設定原本的類型

            // 核心防護線：如果這次修改沒有傳入新圖片（updateRecipe 裡的 imageUrl 為 null）
            // 則自動將資料庫撈出來的舊圖片路徑補回去，避免原本的圖片不小心被抹除
            if (updateRecipe.getImageUrl() == null) {
                updateRecipe.setImageUrl(existingRecipe.getImageUrl());
            }

            recipeRepository.save(updateRecipe);

        } else {
            throw new SecurityException("您沒有權限修改此食譜！");
        }
    }

    /**
     * --- Service 層 ---
     * 方法名稱：deleteRecipe(傳入變數：要刪除的 Integer id, 傳入變數：當前登入者 String username)
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
    public void deleteRecipe(Integer id, String username) {

        Recipe targetRecipe = findById(id);

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
            String author) {

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

        // 優先權 4：作者搜尋
        if (author != null && !author.isEmpty()) {
            List<Recipe> recipes = recipeRepository.findByAuthor(author);
            resultMap.put("👨‍🍳 " + author + " 的專屬食譜", recipes);
            return resultMap;
        }

        // 預設：什麼都沒選，顯示探索食譜畫面
        return getAllRecipes();
    }

    /**
     * 首頁右側的Diy食譜列表
     * 獲取使用者最新 5 筆 DIY 食譜
     */
    public List<Recipe> getTopLatestDiyRecipes(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>(); // 防呆：沒登入就給空籃子
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
