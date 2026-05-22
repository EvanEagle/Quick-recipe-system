package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.model.Recipe;
import com.example.quick_recipe_system.model.cuisine.ChineseCuisine;
import com.example.quick_recipe_system.model.cuisine.CuisineType;
import com.example.quick_recipe_system.model.cuisine.JapaneseCuisine;
import com.example.quick_recipe_system.model.cuisine.WesternCuisine;

@Service
public class RecipeService {

    private List<CuisineType> cuisineTypes = new ArrayList<>();

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
        for (Recipe recipe :  getAllRecipes()) {
            // 如果找到 ID 一樣的食譜
            if (recipe.getId().equals(id)) {
                return recipe; // 找到了！立刻把這道食譜交出去
            }
        }
        return null; // 如果整圈找完都沒找到 (例如傳入一個不存在的 ID)，就回傳 null
    }

}
