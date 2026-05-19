package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.model.Recipe;
import com.example.quick_recipe_system.model.cuisine.ChineseCuisine;
import com.example.quick_recipe_system.model.cuisine.CuisineType;
import com.example.quick_recipe_system.model.cuisine.JapaneseCuisine;
import com.example.quick_recipe_system.model.cuisine.WesternCuisine;

@Service
public class RecipeService {

    private List<CuisineType> cuisineTypes = new ArrayList<>();

    private RecipeService() {
        CuisineType chinese = new ChineseCuisine();
        CuisineType japanese = new JapaneseCuisine();
        CuisineType Western = new WesternCuisine();

        cuisineTypes.add(chinese);
        cuisineTypes.add(japanese);
        cuisineTypes.add(Western);
    }

    //取得所有食譜類型
    public List<CuisineType> getAllCuisineTypes() {
        return cuisineTypes;
    }

    //取得所有食譜
    public List<Recipe> getAllRecipes() {

        //先把所有食譜放在allRecipes裡
        List<Recipe> allRecipes = new ArrayList<>();

        //用foreach 把cuisineTypes中的每一個類型叫出來
        for (CuisineType type : cuisineTypes)  {
            //在把每個type中的食譜取出,放入 allRecipes裡
            allRecipes.addAll(type.getRecipes());
        }
        //最後return 每一道食譜
        return allRecipes;
    }

}
