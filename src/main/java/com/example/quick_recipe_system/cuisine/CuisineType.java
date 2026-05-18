package com.example.quick_recipe_system.cuisine;

import java.util.List;

import com.example.quick_recipe_system.model.Recipe;

//CuisineType 只是盒子, 分類料理的類型
public interface CuisineType {
    String getCuisineName(); //料理類型名稱
    List<Recipe> getRecipes(); //取得該類型所有食譜
    void addRecipe(Recipe recipe); //加入食譜
   

}
