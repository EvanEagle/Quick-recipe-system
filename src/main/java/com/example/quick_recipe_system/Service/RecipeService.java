package com.example.quick_recipe_system.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.quick_recipe_system.model.Recipe;

@Service
public class RecipeService {
    
    private List<Recipe> mockDatabase = new ArrayList<>();

    public RecipeService() {
        // 初始化一些假資料
        mockDatabase.add(new Recipe(
            1, 
            "日式章魚燒", 
            25, 
            Arrays.asList("章魚塊", "高麗菜", "章魚燒專用粉", "雞蛋", "水"), 
            Arrays.asList("柴魚片", "海苔粉", "章魚燒醬", "日式美乃滋"), 
            Arrays.asList("將粉、蛋、水混合成麵糊", "麵糊倒入烤盤，加入章魚與高麗菜", "用竹籤翻轉至金黃圓潤"), 
            Arrays.asList("日式", "點心", "章魚"), 
            "https://www.youtube.com/embed/dQw4w9WgXcQ" // 替換成真實的 YouTube Embed 連結
        ));

        mockDatabase.add(new Recipe(
            2, 
            "美式脆皮炸雞", 
            40, 
            Arrays.asList("帶骨雞腿肉", "美式炸雞粉", "牛奶", "雞蛋"), 
            Arrays.asList("鹽巴", "黑胡椒", "蒜粉", "紅椒粉"), 
            Arrays.asList("雞肉用牛奶與香料醃製2小時", "均勻裹上炸雞粉", "170度高溫油炸至金黃酥脆"), 
            Arrays.asList("美式", "炸物", "雞肉", "派對"), 
            "https://www.youtube.com/embed/dQw4w9WgXcQ" 
        ));
    }

    // 提供給 Controller 呼叫的方法：取得所有食譜
    public List<Recipe> getAllRecipes() {
        return mockDatabase;
    }
}
