package com.example.quick_recipe_system.manager;

import java.util.ArrayList;
import java.util.List;

import com.example.quick_recipe_system.model.Recipe;

public class FavoriteManager {

    private List<Recipe> favoriteRecipes = new ArrayList<>();

    public List<Recipe> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public boolean addFavoriteRecipes(Recipe recipe) {
        if (favoriteRecipes.contains(recipe)) {
            System.out.println("此料理已經在收藏清單囉！\n");
            return false;
        } else {
            favoriteRecipes.add(recipe);
            System.out.println("已收藏「" + recipe.getName() + "」\n");
            return true;
        }
    }

    public void removeFavorite(int id) {
        if (id < 1 || id > favoriteRecipes.size()) {
            System.out.println("你選擇的編號不存在,請重新選擇");
            return;
        }

        String removeName = favoriteRecipes.get(id - 1).getName();
        favoriteRecipes.remove(id - 1);
        System.out.println("已成功刪除料理「" + removeName + "」\n");
    }
}
