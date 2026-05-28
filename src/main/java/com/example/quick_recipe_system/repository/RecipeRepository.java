package com.example.quick_recipe_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quick_recipe_system.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    /**
     * 1. 供 DIY 面板使用：找出特定作者的食譜
     * 翻譯 SQL：SELECT * FROM recipe WHERE author = ?
     */
    List<Recipe> findByAuthor(String author);

    /**
     * 2. 供首頁使用：利用料理類型(中/日/西式)搜尋
     * 翻譯 SQL：SELECT * FROM recipe WHERE type_string = ?
     */
    List<Recipe> findByTypeString(String typeString);

    /**
     * 3. 供首頁使用：利用料理時間搜尋 (尋找小於等於該時間的食譜)
     * 翻譯 SQL：SELECT * FROM recipe WHERE cooking_time <= ?
     */
    List<Recipe> findByCookingTimeLessThanEqual(int time);

    /**
     * 4. 供首頁使用：利用關鍵字搜尋食譜名稱 (模糊搜尋)
     * 翻譯 SQL：SELECT * FROM recipe WHERE name LIKE %?%
     */
    List<Recipe> findByNameContaining(String keyword);

}
