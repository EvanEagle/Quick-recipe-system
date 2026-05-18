package com.example.quick_recipe_system.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.example.quick_recipe_system.cuisine.AbstractCuisine;
import com.example.quick_recipe_system.model.Recipe;

public class RecipeSearcher {

    public List<Recipe> searchByCookingTime(List<Recipe> allRecipes, int maxTime) {
        List<Recipe> foundRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (recipe.getCookingTime() <= maxTime) {
                foundRecipes.add(recipe);
            }
        }
        foundRecipes.sort(Comparator.comparingInt(Recipe::getCookingTime)); // 搜尋出來結果按照時間小到大排序
        return foundRecipes;
    }

    public List<Recipe> searchByKeyword(List<Recipe> allRecipes, String keyword) {
        List<Recipe> foundRecipes = new ArrayList<>();

        keyword = keyword.trim();

        for (Recipe recipe : allRecipes) {

            boolean matched = false;

            for (String key : recipe.getKeywords()) {
                if (key.contains(keyword)) { // 為什麼使用contains -> 使用者輸入"蛋",也可以搜尋的到雞蛋
                    matched = true;
                    break; // 找到符合關鍵字的食譜就不用再找了,使用break一道菜搜尋一次就夠,避免重複加入
                }
            }

            if (!matched) {
                for (String recipe2 : recipe.getIngredients()) {
                    if (recipe2.contains(keyword)) {
                        matched = true;
                        break;
                    }
                }
            }
            if (matched) {
                foundRecipes.add(recipe);
            }
        }
        return foundRecipes;
    }

    public List<Recipe> searchByCuisineType(Map<String, AbstractCuisine> cuisineMap, String type) {

        AbstractCuisine cuisine = cuisineMap.get(type);

        if (cuisine == null) {
            System.out.println("找不到此料理類型");
            return new ArrayList<>();
        }

        List<Recipe> recipes = cuisine.getRecipes();

        if (recipes.isEmpty()) {
            System.out.println("此類型目前沒有食譜");
            return new ArrayList<>();
        }
        for (int i = 0; i < recipes.size(); i++) {
            System.out.println((i + 1) + ". " + recipes.get(i).getName());
        }
        return recipes;
    }

    public List<Recipe> getRecipesFromCuisine(AbstractCuisine cuisine) {
        if (cuisine == null)return new ArrayList<>();
        return cuisine.getRecipes();
    }
}
