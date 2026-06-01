package com.example.quick_recipe_system.entity;

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
    private Integer id;

    @Column(name = "type_string", nullable = false)
    private String typeString;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "cooking_time", nullable = false)
    private int cookingTime;

    // === List 必須改用 @ElementCollection ===
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_seasonings", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "seasoning")
    private List<String> seasonings;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step")
    private List<String> steps;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_keywords", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "keyword")
    private List<String> keywords;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "author", nullable = false)
    private String author;

}