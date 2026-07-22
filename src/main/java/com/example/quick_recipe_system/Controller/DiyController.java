package com.example.quick_recipe_system.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.DiyService;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DiyController {

    private final DiyService diyService;
    private final RecipeService recipeService;

    /**
     * --- DIY 頁面 ---
     * 方法名稱：showDiyPage(傳入變數：當前登入者 String username, 傳入變數：食譜 List<Recipe>
     * myDiyRecipes)
     * 
     * 1. 取得當前登入者的食譜
     * 2. 將食譜列表裝進 Model 並呼叫前端頁面
     * 3. 返回 DIY 頁面
     */
    @GetMapping("/diy")
    public String showDiyPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");

        List<Recipe> myDiyRecipes = recipeService.findRecipesByAuthor(username);
        model.addAttribute("diyRecipes", myDiyRecipes);
        return "diy-page";
    }

    /**
     * --- 新增食譜頁面 ---
     * 方法名稱：showAddRecipe
     * 
     * 1. 建立一個新的食譜物件
     * 2. 將食譜物件裝進 Model 並呼叫前端頁面
     * 3. 返回 新增食譜頁面
     */
    @GetMapping("/recipe/add")
    public String showAddRecipe(Model model) {

        model.addAttribute("recipe", new Recipe());
        return "recipe-add";
    }

    /**
     * --- 新增食譜頁面 ---
     * 方法名稱：addRecipe(傳入變數：食譜 Recipe recipe, 傳入變數：類型 String typeString)
     * 
     * 1. 取得當前登入者的食譜
     * 2. 將食譜列表裝進 Model 並呼叫前端頁面
     * 3. 返回 DIY 頁面 (新增頁面)
     */
   @PostMapping("/recipe/add")
    public String addRecipe(
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session,
            @ModelAttribute Recipe recipe,
            String typeString,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        try {
            diyService.createDiyRecipe(recipe, typeString, imageFile, username);
            
            redirectAttributes.addFlashAttribute("successMsg", "新增食譜成功！");
            return "redirect:/diy";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/recipe/add";
        }
    }

    @GetMapping("recipe/edit/{id}")
    public String showEditRecipeForm(@PathVariable Long id, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        Recipe findId = recipeService.findById(id);

        if (findId == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "找不到該食譜！");
            return "redirect:/diy";
        }

        if (!username.equals(findId.getAuthor())) {
            redirectAttributes.addFlashAttribute("errorMsg", "您無權修改別人的食譜！");
            return "redirect:/diy";
        }

        model.addAttribute("recipe", findId);
        return "recipe-edit";
    }

    @PostMapping("/recipe/edit/{id}")
    public String updateRecipe(
            @PathVariable Long id,
            @ModelAttribute Recipe updateRecipe, // 微調 1：補上 @ModelAttribute 讓 Spring 綁定更明確
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");
        
        try {
            diyService.updateDiyRecipe(updateRecipe, username, imageFile);
            redirectAttributes.addFlashAttribute("successMsg", "食譜修改成功！");
            return "redirect:/diy";
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/diy";
        }
        
    }

    // 攔截帶有 ID 的 GET 請求
    @GetMapping("/recipe/delete/{id}")
    public String deleteRecipeFallback(@PathVariable(required = false) Long id,
            RedirectAttributes redirectAttributes) {
        // 亂闖移除網址，直接踢回他的 DIY 管理清單
        redirectAttributes.addFlashAttribute("errorMsg", "請透過正常的按鈕來移除食譜喔！");
        return "redirect:/diy";
    }

    // (選用)攔截連 ID 都沒打，只打一半網址的 GET 請求
    @GetMapping("/recipe/delete")
    public String deleteRecipeNoIdFallback(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", "無效的操作路徑！");
        return "redirect:/diy";
    }

    @PostMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        try {
            diyService.deleteDiyRecipe(id, username);
            redirectAttributes.addFlashAttribute("successMsg", "食譜已成功刪除！");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/diy";
    }
}
