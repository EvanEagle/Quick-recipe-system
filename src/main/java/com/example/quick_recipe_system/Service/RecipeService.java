package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;

    /**
     * 取得所有食譜
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Map<String, List<Recipe>> getRecipesByType() {
        // 1. 從資料庫撈出所有食譜
        List<Recipe> allRecipes = recipeRepository.findAll();

        Map<String, List<Recipe>> rawMap = allRecipes.stream().collect(Collectors.groupingBy(Recipe::getTypeString));

        // 3. 建立一個全新的 LinkedHashMap（關鍵：它會記住你 put 的先後順序！）
        Map<String, List<Recipe>> orderedMap = new LinkedHashMap<>();

        // 4. 依照你指定的順序放入，同時把英文 Key 換成中文 Key
        // 使用 getOrDefault 是為了防呆：如果資料庫目前剛好沒有某類的菜，就給它一個空清單，畫面就不會出錯
        orderedMap.put("中式", rawMap.getOrDefault("chinese",
                rawMap.getOrDefault("Chinese", rawMap.getOrDefault("中式", new ArrayList<>()))));
        orderedMap.put("日式", rawMap.getOrDefault("japanese",
                rawMap.getOrDefault("Japanese", rawMap.getOrDefault("日式", new ArrayList<>()))));
        orderedMap.put("西式", rawMap.getOrDefault("western",
                rawMap.getOrDefault("Western", rawMap.getOrDefault("西式", new ArrayList<>()))));

        return orderedMap;
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

    public void updateRecipe(Recipe updateRecipe, String username) {

        Recipe existingRecipe = findById(updateRecipe.getId());

        if (existingRecipe != null && existingRecipe.getAuthor().equals(username)) {

            updateRecipe.setAuthor(username); // 將食譜的作者設定為原本的作者
            updateRecipe.setTypeString(existingRecipe.getTypeString()); // 料理類型設定原本的類型
            recipeRepository.save(updateRecipe);

        } else {
            throw new SecurityException("您沒有權限修改此食譜！");
        }
    }

    /*
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
    public void deleteRecipe(Integer id, String username) {

        Recipe targetRecipe = findById(id);

        if (targetRecipe != null && targetRecipe.getAuthor().equals(username)) {
            recipeRepository.delete(targetRecipe);
        } else {
            throw new SecurityException("您沒有權限刪除此食譜！");
        }
    }
    /**
     * 解析首頁的烹調時間字串，並呼叫對應的 JPA 查詢
     */
    public Map<String, List<Recipe>> searchByCookingTimeStr(String timeStr) {
        List<Recipe> sortedRecipes;
        String customLabel;

        // 進行字串比對與翻譯
        if ("10mins".equals(timeStr)) {
            sortedRecipes = recipeRepository.findByCookingTimeLessThanEqualOrderByCookingTimeAsc(10);
            customLabel = "⏱️ 10分鐘快手";
        } else if ("20mins".equals(timeStr)) {
            sortedRecipes = recipeRepository.findByCookingTimeLessThanEqualOrderByCookingTimeAsc(20);
            customLabel = "⏱️ 20分鐘輕鬆上菜";
        } else if ("30mins".equals(timeStr)) {
            sortedRecipes = recipeRepository.findByCookingTimeLessThanEqualOrderByCookingTimeAsc(30);
            customLabel = "⏱️ 30分鐘經典";
        } else if ("30mins up".equals(timeStr)) {
            // 注意：這裡是呼叫 GreaterThan (大於)
            sortedRecipes = recipeRepository.findByCookingTimeGreaterThanOrderByCookingTimeAsc(30);
            customLabel = "🍳 30分鐘以上功夫菜";
        } else {
            // 如果選到 "time" 或是網址被亂打，預設回傳全部分類
            return getRecipesByType(); 
        }

        // 把結果裝進 Map 回傳
        Map<String, List<Recipe>> resultMap = new LinkedHashMap<>();
        resultMap.put(customLabel, sortedRecipes);
        return resultMap;
    }

    
    public Map<String, List<Recipe>> masterSearch(String keyword, String cookingtime, String author, String typeString) {
        
        Map<String, List<Recipe>> resultMap = new LinkedHashMap<>();

        // 優先權 1：關鍵字綜合搜尋 (菜名、食材、標籤)
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Recipe> recipes = recipeRepository.searchByComprehensiveKeyword(keyword);
            resultMap.put("🔍 搜尋結果：'" + keyword + "'", recipes);
            return resultMap;
        }

        // 優先權 2：烹調時間搜尋 (沿用昨天寫好的邏輯)
        if (cookingtime != null && !cookingtime.isEmpty()) {
            return searchByCookingTimeStr(cookingtime); 
        }

        // 優先權 3：料理類型搜尋
        if (typeString != null && !typeString.isEmpty()) {
            // 假設你有一個 findByTypeString 的 Repository 方法
            List<Recipe> recipes = recipeRepository.findByTypeString(typeString);
            resultMap.put(typeString , recipes);
            return resultMap;
        }
        
        // 優先權 4：作者搜尋
        if (author != null && !author.isEmpty()) {
            List<Recipe> recipes = recipeRepository.findByAuthor(author);
            resultMap.put("👨‍🍳 " + author + " 的專屬食譜", recipes);
            return resultMap;
        }

        // 預設：什麼都沒選，顯示中/日/西式大分類
        return getRecipesByType(); 
    }

    /**
     * 獲取使用者最新 5 筆 DIY 私房食譜
     */
    public List<Recipe> getTopLatestDiyRecipes(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ArrayList<>(); // 防呆：沒登入就給空籃子
        }
        return recipeRepository.findTop5ByAuthorOrderByIdDesc(username);
    }
}
