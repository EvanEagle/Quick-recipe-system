package com.example.quick_recipe_system.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_string", nullable = false)
    private String typeString;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "cooking_time", nullable = false)
    private int cookingTime;

    // === List 必須改用 @ElementCollection ===
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_seasonings", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "seasoning")
    private List<String> seasonings;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step")
    private List<String> steps;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recipe_keywords", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 在寫入資料庫前，自動把時間填入 (這叫 JPA 生命週期回呼)
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 紀錄這篇食譜被收藏的次數，預設為 0
    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount = 0;

    /**
     * 食譜類型：true 為官方系統食譜，false 為一般會員 DIY 食譜
     */
    @Column(name = "is_system_recipe", nullable = false)
    private boolean systemRecipe = false;
}