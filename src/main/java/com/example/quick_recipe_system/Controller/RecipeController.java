package com.example.quick_recipe_system.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.RecipeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * 導覽列-探索食譜(取得所有食譜並按照類型排列顯示)
     */
    @GetMapping("/recipe")
    public String findAllRecipes(Model model) {

        Map<String, List<Recipe>> allRecipesByType = recipeService.getAllRecipes();

        model.addAttribute("recipes", allRecipesByType);
        return "recipe-list";
    }

    /**
     * 處理食譜搜尋與篩選請求
     * 
     * @param keyword     綜合關鍵字（對應大搜尋框）
     * @param cookingtime 烹調時間（對應時間篩選器）
     * @param typeString  料理類型（對應中/日/西式按鈕）
     * @param source      食譜來源（目前支援 official 官方食譜）
     */
    @GetMapping("/search")
    public String searchRecipes(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "cookingtime", required = false) String cookingtime,
            @RequestParam(value = "typeString", required = false) String typeString,
            @RequestParam(value = "source", required = false) String source,
            Model model) {

        // 1. 呼叫 Service 的萬能搜尋方法，取得篩選後的 Map 資料
        Map<String, List<Recipe>> searchResults = recipeService.masterSearch(keyword, cookingtime, typeString, source);

        // 2. 將結果放入 Model。
        model.addAttribute("recipes", searchResults);

        // 3. 與探索食譜共用同一個 HTML 模板頁面
        return "recipe-list";
    }

    /**
     * 查看食譜詳情
     */
    @GetMapping("/recipe/detail/{id}")
    public String showRecipeDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {

        // 1. 透過 Service 去資料庫把這道食譜的完整資料撈出來
        Recipe recipe = recipeService.findById(id);

        // 防呆機制1 : 如果有人亂輸入網址 ID，找不到食譜，就直接踢回探索列表
        if (recipe == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "找不到該食譜");
            return "redirect:/recipe";
        }
        // 2. 把撈出來的完整食譜資料裝進 Model，準備送給前端畫面
        model.addAttribute("recipe", recipe);

        // 3. 導向食譜詳情頁面
        return "recipe-list-detail";
    }

}