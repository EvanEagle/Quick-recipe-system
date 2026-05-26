package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.model.Recipe;
import com.example.quick_recipe_system.model.cuisine.ChineseCuisine;
import com.example.quick_recipe_system.model.cuisine.CuisineType;
import com.example.quick_recipe_system.model.cuisine.JapaneseCuisine;
import com.example.quick_recipe_system.model.cuisine.WesternCuisine;

@Service
public class RecipeService {

    private List<CuisineType> cuisineTypes = new ArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(16);

    private RecipeService() {
        CuisineType chinese = new ChineseCuisine();
        CuisineType japanese = new JapaneseCuisine();
        CuisineType Western = new WesternCuisine();

        cuisineTypes.add(chinese);
        cuisineTypes.add(japanese);
        cuisineTypes.add(Western);
    }

    // 取得所有食譜類型
    public List<CuisineType> getAllCuisineTypes() {
        return cuisineTypes;
    }

    // 取得所有食譜
    public List<Recipe> getAllRecipes() {

        // 先把所有食譜放在allRecipes裡
        List<Recipe> allRecipes = new ArrayList<>();

        // 用foreach 把cuisineTypes中的每一個類型叫出來
        for (CuisineType type : cuisineTypes) {
            // 在把每個type中的食譜取出,放入 allRecipes裡
            allRecipes.addAll(type.getRecipes());
        }
        // 最後return 每一道食譜
        return allRecipes;
    }

    // 根據 ID 尋找特定的食譜
    public Recipe findById(Integer id) {

        // 遍歷你的 15 道食譜清單 (請確認你的清單變數名稱，這裡是假設為 allRecipes)
        for (Recipe recipe : getAllRecipes()) {
            // 如果找到 ID 一樣的食譜
            if (recipe.getId().equals(id)) {
                return recipe; // 找到了！立刻把這道食譜交出去
            }
        }
        return null; // 如果整圈找完都沒找到 (例如傳入一個不存在的 ID)，就回傳 null
    }

    public List<Recipe> findRecipesByAuthor(String username) {
        List<Recipe> myRecipes = new ArrayList<>();

        for (Recipe recipe : getAllRecipes()) {
            if (recipe != null && recipe.getAuthor().equals(username)) {
                myRecipes.add(recipe);
            }
        }
        return myRecipes;
    }

    /**
     * 新增食譜
     * 
     * @param newRecipe  由 Controller 傳來，已經裝滿使用者填寫資料的食譜物件 (唯獨沒有 id)
     * @param typeString 前端表單傳來的分類字串 (例如："Chinese", "Japanese", "Western")
     */
    public void addRecipe(Recipe newRecipe, String typeString) {

        // 步驟 1：賦予這道新食譜一個唯一的 ID
        newRecipe.setId(idGenerator.getAndIncrement());

        // 步驟 2：遍歷我們現有的分類，尋找對應的 CuisineType
        for (CuisineType type : cuisineTypes) {

            // 這裡使用類別名稱來比對。
            // 例如 type.getClass().getSimpleName() 會得到 "ChineseCuisine"
            // 如果前端傳來的 typeString 是 "Chinese"，就代表找到了
            if (type.getClass().getSimpleName().contains(typeString)) {

                // 步驟 3：找到正確的分類後，將這道新食譜加入該分類的清單中
                type.addRecipe(newRecipe);
                return; // 新增成功，結束這個方法
            }
        }

        // 防呆機制：如果前端傳來一個不存在的分類名稱，可以拋出例外，或是預設加到中式料理
        throw new IllegalArgumentException("找不到對應的食譜分類：" + typeString);
    }

    public void updateRecipe(Integer id, Recipe newRecipe) {

        Recipe oldRecipe = findById(id);

        if (oldRecipe != null) {
            oldRecipe.setName(newRecipe.getName());
            oldRecipe.setCookingTime(newRecipe.getCookingTime());
            oldRecipe.setIngredients(newRecipe.getIngredients());
            oldRecipe.setSeasonings(newRecipe.getSeasonings());
            oldRecipe.setSteps(newRecipe.getSteps());
            oldRecipe.setKeywords(newRecipe.getKeywords());
            oldRecipe.setVideoUrl(newRecipe.getVideoUrl());
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
     * 使用迴圈把 cuisineTypes (中、日、西式分類) 一個一個拿出來
     * 把每個分類裡面的 Recipe 清單叫出來
     * 從清單中，把「ID 等於我們傳入的 id」的那道食譜給剔除掉
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
            for (CuisineType cuisineType : cuisineTypes) {
                cuisineType.getRecipes().removeIf(recipe -> recipe.getId().equals(id));
            }
        } else {
            throw new SecurityException("您沒有權限刪除此食譜！");
        }
    }
}
