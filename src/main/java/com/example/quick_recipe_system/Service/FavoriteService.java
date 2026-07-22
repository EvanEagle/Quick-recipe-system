package com.example.quick_recipe_system.service;

import java.util.List;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quick_recipe_system.entity.Favorite;
import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.repository.FavoriteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    /**
     * 1. 取得特定使用者的收藏清單
     */
    public List<Recipe> getFavoriteRecipe(String username) {
        // 步驟 A：去資料庫把這個人的所有 Favorite (收藏實體) 撈出來
        List<Favorite> favorites = favoriteRepository.findByUsername(username);
        
        // 步驟 B：把 Favorite 裡面的 Recipe 抽出來，打包成一個新的 List 回傳給前端
        return favorites.stream()
                .map(favorite -> favorite.getRecipe())
                .collect(Collectors.toList());
    }

    /**
     * 2. 加入收藏邏輯(判斷是否收藏過)
     */
    public boolean addFavorite(String username, Recipe recipe) {
        if (favoriteRepository.existsByUsernameAndRecipeId(username, recipe.getId())) {
            return false; // 已經收藏過了
        }

        Favorite newFavorite = new Favorite();
        newFavorite.setUsername(username);
        newFavorite.setRecipe(recipe);

        favoriteRepository.save(newFavorite);
        return true;
    }

    /**
     * 3. 移除收藏
     */
    @Transactional // 關鍵：在 JPA 中執行 Delete 或 Update 操作，必須掛上 Transactional 確保交易安全
    public void removeFavorite(String username, Long recipeId) {
       
        favoriteRepository.deleteByUsernameAndRecipeId(username, recipeId);
    }
}
