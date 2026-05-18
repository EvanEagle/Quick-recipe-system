package com.example.quick_recipe_system.cuisine;

import java.util.ArrayList;
import java.util.List;

import com.example.quick_recipe_system.model.Recipe;

public abstract class AbstractCuisine implements CuisineType{

    //用protected,因為子類別(中式/日式/西式)會用到
    protected List<Recipe> recipes = new ArrayList<>();

    //留給子類別實作(這邊不實做,定義成抽象方法)
    @Override
    public abstract String getCuisineName();

    @Override
    public List<Recipe> getRecipes() {
        return recipes;
    }

    @Override
    public void addRecipe(Recipe recipe) {
       recipes.add(recipe);
    }

}
