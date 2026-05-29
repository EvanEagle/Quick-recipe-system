package com.example.quick_recipe_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * 3. 供首頁使用：利用料理時間搜尋 (找烹調時間「小於等於」，並由小到大排序)
     * 翻譯 SQL：SELECT * FROM recipe WHERE cooking_time <= ?
     */
    List<Recipe> findByCookingTimeLessThanEqualOrderByCookingTimeAsc(Integer maxTime);

    /**
     * 3. 供首頁使用：利用料理時間搜尋 (找烹調時間「大於 (GreaterThan)」，並由小到大排序)
     */
    List<Recipe> findByCookingTimeGreaterThanOrderByCookingTimeAsc(Integer time);

    /**
     * 4. 使用 JPQL 進行關聯表的模糊搜尋 (LIKE)
     * 這行會同時去比對 Recipe 的名字，以及關聯的 ingredients 和 keywords 表格！
     */
    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN r.ingredients i LEFT JOIN r.keywords k " +
            "WHERE r.name LIKE %:keyword% OR i LIKE %:keyword% OR k LIKE %:keyword%")
    List<Recipe> searchByComprehensiveKeyword(@Param("keyword") String keyword);


    List<Recipe> findTop5ByAuthorOrderByIdDesc(String author);

}
