package com.example.quick_recipe_system.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    private Integer id; // 【新增】唯一識別碼，方便未來串接資料庫與購物車
    private String name; // 名稱
    private int cookingTime; // 烹調時間
    private List<String> ingredients; // 主食材
    private List<String> seasonings; // 調味料
    private List<String> steps; // 作法
    private List<String> keywords; // 關鍵字(加了這個之後可以用任何關鍵字去搜尋食譜)
    private String videoUrl; // 【新增】YouTube 影片連結
    private String author; // 記錄建立此食譜的使用者帳號 (與 session.loggedInUser 對應)
}