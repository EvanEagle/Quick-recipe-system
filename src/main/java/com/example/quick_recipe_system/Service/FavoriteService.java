package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.model.Recipe;

@Service
public class FavoriteService {

    /**
     * 建立一個大置物櫃(Map), 可以塞很多使用者的收藏清單
     * Key 是帳號 (username)，Value 是該帳號的收藏清單
     */
    private Map<String, List<Recipe>> userFavorites = new HashMap<>();

    /**
     * 1.取得特定使用者的收藏清單
     * 如果該帳號還沒有建檔，就回傳一個空的 ArrayList，避免 NullPointerException
     * 補充: getOrDefault方法-返回映射到HashMap中指定鍵的值(username)。
     * 如果該鍵不存在，則返回默認值(new ArrayList<>())。
     * getOrDefault只回傳值, 不寫入
     * 在這裡的用法: 如果找不到 username 的資料，就直接回傳 new ArrayList<>()
     */
    public List<Recipe> getFavoriteRecipe(String username) {
        return userFavorites.getOrDefault(username, new ArrayList<>());
    }

    /**
     * 2. 加入收藏
     * 如果這個帳號沒有收藏過，使用putIfAbsent幫他在置物櫃裡開一個專屬空間
     * if判斷式-檢查食譜是否已經收藏過了(Recipe 類別一定要有重寫 equals 和 hashCode 方法)
     * 補充: putIfAbsent方法-如果指定的鍵（Key）不存在於 Map 中，則將鍵值對放入 Map；
     * 如果鍵已經存在，則不做任何操作。
     * 在這裡的用法: 如果 username 不存在，就放入 new ArrayList<>()；如果已存在，就不做任何事
     */
    public boolean addFavorite(String username, Recipe recipe) {
        userFavorites.putIfAbsent(username, new ArrayList<>());

        List<Recipe> favorites = userFavorites.get(username);

        if (favorites.contains(recipe)) {
            return false;
        }
        favorites.add(recipe);
        return true;
    }

    /**
     * 3. 移除收藏 (為傳入帳號與食譜id來精準刪除)
     * 51行-使用 Java 8 的 removeIf 語法，尋找名稱相符的食譜並移除
     */
    public void removeFavorite(String username, Integer id) {
        List<Recipe> favorites = getFavoriteRecipe(username);
        favorites.removeIf(recipe -> recipe.getId().equals(id));
    }

}
