package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.FavoriteService;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // 註解 @RequiredArgsConstructor 會自動將 final 物件注入, 不用再寫 @Autowired
public class FavoriteController {

    
    private final FavoriteService favoriteService;
    private final RecipeService recipeService;

    /**
     * 用 HttpSession 確認用戶是否有登入
     * 未登入使用 LoginInterceptor 攔截
     * 有登入從 favoriteService 取出 使用者的收藏清單
     * 裝入 model 的箱子, 上面貼 "favorites" 標籤
     * 傳送到 favorite.html
     */
    @GetMapping("/favorite")
    public String showFavoritesPage(HttpSession session, Model model) {

        String username = (String) session.getAttribute("loggedInUser");
        
        List<Recipe> myFavorites = favoriteService.getFavoriteRecipe(username);
        model.addAttribute("favorites", myFavorites);

        return "favorite";
    }

    
    @GetMapping("/favorite/add")
    public String addFavoriteFallback(RedirectAttributes redirectAttributes) {
        // 如果使用者用 GET 亂闖，給個溫馨提示，並踢回食譜列表頁
        redirectAttributes.addFlashAttribute("errorMsg", "請透過正常的按鈕來加入收藏喔！");
        return "redirect:/recipe"; 
    }

    @PostMapping("/favorite/add")
    public String addFavorite(@RequestParam Integer recipeId,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) { // 負責抓取使用者是從哪頁點擊的

        String username = (String) session.getAttribute("loggedInUser");

        // 2. 根據 ID 找出那道食譜
        Recipe recipe = recipeService.findById(recipeId);

        // 3. 呼叫 FavoriteService 把食譜存進資料庫
        if (recipe != null) {
            boolean isAdded = favoriteService.addFavorite(username, recipe);

            // 4. 根據結果，給予對應的提示訊息
            if (isAdded) {
                redirectAttributes.addFlashAttribute("successMsg", "🎉 已成功將「" +
                        recipe.getName() + "」加入收藏！");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "這道料理已經在你的收藏清單中囉！");
            }
        }

        /** 
         * 5.把使用者踢回他原本點擊按鈕的那一頁！
         * referer: 是 HTTP 請求標頭的一個欄位，用來告訴伺服器目前的請求是從哪個頁面（URL）點擊或跳轉而來的
         * 從哪裡來，就回哪裡去
         * 使用三元運算子檢查referer是否為null
         */ 
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/favorite/remove")
    public String removeFavoriteFallback(RedirectAttributes redirectAttributes) {
        // 亂闖移除網址，直接踢回他的收藏清單
        redirectAttributes.addFlashAttribute("errorMsg", "請透過正常的按鈕來移除收藏喔！");
        return "redirect:/favorite"; 
    }

    // 3. 處理「取消收藏」的動作
    @PostMapping("/favorite/remove")
    public String removeFavorite(@RequestParam Integer recipeId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        // 2. 為了在畫面上顯示貼心的提示，我們先查出這道菜的名字
        Recipe recipe = recipeService.findById(recipeId);
        String recipeName = (recipe != null) ? recipe.getName() : "該食譜不存在";

        // 3. 呼叫 FavoriteService 把這道食譜從資料庫移除
        favoriteService.removeFavorite(username, recipeId);

        redirectAttributes.addFlashAttribute("successMsg", "💔 已將「" + recipeName + "」從收藏中移除！");

        return "redirect:/favorite";
    }

}
