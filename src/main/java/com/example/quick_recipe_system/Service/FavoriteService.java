package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.model.Recipe;

@Service
public class FavoriteService {

    private Map<String, List<Recipe>> userFavorites = new HashMap<>();

    public List<Recipe> getFavoriteRecipe(String username) {
        return userFavorites.getOrDefault(username, new ArrayList<>());
    }

    public boolean addFavorite(String username, Recipe recipe) {
        userFavorites.putIfAbsent(username, new ArrayList<>());

        List<Recipe> favorites = userFavorites.get(username);

        if (favorites.contains(recipe)) {
            return false;
        } 
        favorites.add(recipe);
        return true;
    }

    public void removeFavorite(String username, String resipeName) {
        List<Recipe> favorites = getFavoriteRecipe(username);
        favorites.removeIf(recipe -> recipe.getName().equals(resipeName));
    }
}
