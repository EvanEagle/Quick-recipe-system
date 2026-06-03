package com.example.quick_recipe_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.example.quick_recipe_system.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    // 檢查是否重複
    boolean existsByUsernameAndRecipeId(String username, Integer recipeId);

    //  新增 1：找出這個人的所有收藏紀錄
    List<Favorite> findByUsername(String username);

    //  新增 2：根據帳號與食譜 ID 刪除收藏紀錄
    void deleteByUsernameAndRecipeId(String username, Integer recipeId);

    @Transactional // 標記這是一個會更動資料庫的交易動作
    void deleteByRecipeId(Integer recipeId);
}
