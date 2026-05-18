// package com.example.quick_recipe_system.manager;
// import java.util.ArrayList;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;

// import com.example.quick_recipe_system.cuisine.AbstractCuisine;
// import com.example.quick_recipe_system.cuisine.ChineseCuisine;
// import com.example.quick_recipe_system.cuisine.CuisineType;
// import com.example.quick_recipe_system.cuisine.JapaneseCuisine;
// import com.example.quick_recipe_system.cuisine.WesternCuisine;
// import com.example.quick_recipe_system.model.Recipe;

// public class RecipeRepository {

//     private Map<String, AbstractCuisine> cuisineMap;

//     public RecipeRepository() {

//         cuisineMap = new LinkedHashMap<>();
//         cuisineMap.put("中式料理", new ChineseCuisine());
//         cuisineMap.put("日式料理", new JapaneseCuisine());
//         cuisineMap.put("西式料理", new WesternCuisine());
//     }

//     public List<Recipe> getAllRecipes() {
//         List<Recipe> allRecipes = new ArrayList<>();

//         for (CuisineType cuisineType : cuisineMap.values()) {
//             allRecipes.addAll(cuisineType.getRecipes());
//         }

//         return allRecipes;
//     }

//     public void addRecipeToCuisine(String cuisineName, Recipe recipe) {
//         for (CuisineType cuisine : cuisineMap.values()) {
//             if (cuisine.getCuisineName().equals(cuisineName)) {
//                 cuisine.addRecipe(recipe);
//                 return;
//             }
//         }
//     }

//     public void removeRecipe(String type, int id) {

//         AbstractCuisine cuisine = cuisineMap.get(type);

//         List<Recipe> recipes = cuisine.getRecipes();

//         if (id < 1 || id > recipes.size()) {
//             System.out.println("你選擇的編號不存在,請重新選擇");
//             return;
//         }

//         String removeName = recipes.get(id - 1).getName();
//         recipes.remove(id - 1);
//         System.out.println("已成功刪除料理「" + removeName + "」\n");
//     }

//     public Recipe getRecipe(String type, int id) {

//         AbstractCuisine cuisine = cuisineMap.get(type);

//         List<Recipe> recipes = cuisine.getRecipes();

//         if (id < 1 || id > recipes.size()) {
//             return null;
//         }
//         return recipes.get(id - 1);
//     }

//     /**
//      * 顯示該類型所有食譜
//      */
//     public List<Recipe> showRecipesByCuisine(String type) {

//         AbstractCuisine cuisine = cuisineMap.get(type);

//         List<Recipe> recipes = cuisine.getRecipes();

//         for (int i = 0; i < recipes.size(); i++) {
//             System.out.println((i + 1) + ". " + recipes.get(i).getName());
//         }
//         return recipes;
//     }

//     public AbstractCuisine findCuisineData(String type) {
//         return cuisineMap.get(type);
//     }

//     public Map<String, AbstractCuisine> getCuisineMap() {
//         return cuisineMap;
//     }

    
// }
