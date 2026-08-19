package com.example.quick_recipe_system.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quick_recipe_system.entity.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    /**
     * 供 DIY 面板使用：找出特定作者的食譜
     */
    List<Recipe> findByAuthor(String author);

    /**
     * 供官方食譜搜尋使用
     */
    List<Recipe> findBySystemRecipeTrueOrderByIdDesc();

    /**
     * 供管理員首頁右側面板使用：取得最新5筆官方食譜
     */
    List<Recipe> findTop5BySystemRecipeTrueOrderByIdDesc();

    /**
     * 1.供探索食譜使用
     * 2.供利用料理類型(中/日/西式)搜尋使用
     */
    List<Recipe> findByTypeString(String typeString);

    /**
     * 供搜尋功能使用：利用料理時間搜尋 (找烹調時間「小於等於」，並由小到大排序)
     * 翻譯 SQL：SELECT * FROM recipe WHERE cooking_time <= ?
     */
    List<Recipe> findByCookingTimeLessThanEqualOrderByCookingTimeAsc(Integer maxTime);

    /**
     * 供搜尋功能使用：利用料理時間搜尋 (找烹調時間「大於 (GreaterThan)」，並由小到大排序)
     */
    List<Recipe> findByCookingTimeGreaterThanOrderByCookingTimeAsc(Integer time);

    /**
     * 4. 因同時需比對食譜名稱,食材,關鍵字所以使用 JPQL 進行關聯表的模糊搜尋 (LIKE)
     */
    @Query("SELECT DISTINCT r FROM Recipe r LEFT JOIN r.ingredients i LEFT JOIN r.keywords k " +
            "WHERE r.name LIKE %:keyword% OR i LIKE %:keyword% OR k LIKE %:keyword%")
    List<Recipe> searchByComprehensiveKeyword(@Param("keyword") String keyword);

    /**
     * 供會員首頁使用: 取得最新5筆DIY食譜
     */
    List<Recipe> findTop5ByAuthorOrderByIdDesc(String author);

    /**
     * 供首頁使用: 取得隨機4筆食譜
     */
    @Query(value = "SELECT * FROM recipe ORDER BY RAND() LIMIT 4", nativeQuery = true)
    List<Recipe> findRandom4Recipes();

    /**
     * 儀表板數據 1：查詢「某個時間點之後」新增了多少食譜
     * 之後會在 Controller 傳入「今天的凌晨 00:00:00」給這個參數，
     * 就能輕鬆算出「本日新增食譜數」。
     */
    long countByCreatedAtAfter(LocalDateTime time);

    /**
     * 撈取所有食譜最新的在最上面: 供食譜管理頁面使用
     */
    List<Recipe> findAllByOrderByIdDesc();
}
