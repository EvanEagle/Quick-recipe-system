package com.example.quick_recipe_system.model;
import java.util.List;



public class Recipe {
    private String name; //名稱
    private List<String> ingredients; //主食材
    private List<String> seasonings; //調味料
    private int cookingTime; //烹調時間
    private List<String> steps; //作法
    private List<String> keywords; //關鍵字(加了這個之後可以用任何關鍵字去搜尋食譜)
    

//建構子:
    public Recipe(String name, int cookingTime, List<String> ingredients, List<String> seasonings, List<String> steps, List<String> keywords){
        this.name = name;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.seasonings = seasonings;
        this.steps = steps;
        this.keywords = keywords;
    }

    //方法:
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================================================\n");
        sb.append("\n");
        sb.append("料理名稱: ").append(name).append("\n");
        sb.append("烹調時間: ").append(cookingTime).append(" 分鐘\n");
        sb.append("食材: ").append(String.join(", ", ingredients)).append("\n");
        sb.append("調味料: ").append(String.join(", ", seasonings)).append("\n");
        sb.append("做法: \n");
        for (int i = 0; i < steps.size(); i++) {
            sb.append("  ").append(i + 1).append(". " ).append(steps.get(i)).append("\n");
        }
        
        return sb.toString(); 
    }

    //get/set方法:
    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getSeasonings() {
        return seasonings;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public List<String> getSteps() {
        return steps;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setSeasonings(List<String> seasonings) {
        this.seasonings = seasonings;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}   
