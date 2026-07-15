package com.example.quick_recipe_system.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.example.quick_recipe_system.service.FileStorageService;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final FileStorageService fileStorageService;

    // 因避免使用者上傳非圖片檔的資料,所以定義允許的圖片類型
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

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
     * @param author      作者名稱（保留此參數作為未來社群化功能基礎）
     */
    @GetMapping("/search")
    public String searchRecipes(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "cookingtime", required = false) String cookingtime,
            @RequestParam(value = "typeString", required = false) String typeString,
            @RequestParam(value = "author", required = false) String author,
            Model model) {

        // 1. 呼叫 Service 的萬能搜尋方法，取得篩選後的 Map 資料
        Map<String, List<Recipe>> searchResults = recipeService.masterSearch(keyword, cookingtime, typeString, author);

        // 2. 將結果放入 Model。
        model.addAttribute("recipes", searchResults);

        // 3. 與探索食譜共用同一個 HTML 模板頁面
        return "recipe-list";
    }

    /**
     * 查看食譜詳情
     */
    @GetMapping("/recipe/detail/{id}")
    public String showRecipeDetail(@PathVariable Integer id, RedirectAttributes redirectAttributes, Model model) {

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
            @ModelAttribute Recipe recipe, // 建議補上 @ModelAttribute 綁定表單
            String typeString,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        try {
            recipe.setAuthor(username);

            // 微調 1：先確認使用者「真的有上傳檔案」，才啟動海關檢查格式
            if (imageFile != null && !imageFile.isEmpty()) {
                String contentType = imageFile.getContentType();
                if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
                    // 微調 2：退回 /recipe/add (新增頁面) 比較符合使用者體驗
                    redirectAttributes.addFlashAttribute("errorMsg", "上傳失敗！僅支援 JPG, PNG, GIF, WEBP 格式的照片喔。");
                    return "redirect:/recipe/add";
                }
            }

            // 呼叫輔助方法存圖片 (fileStorageService 裡面已經有判斷 empty 會回傳 null 的邏輯)
            String imageUrl = fileStorageService.saveUploadedImage(imageFile);

            if (imageUrl != null) {
                recipe.setImageUrl(imageUrl); // 有上傳，且格式正確，用新照片
            } else {
                // 使用者沒上傳，給予系統預設圖！
                recipe.setImageUrl("/images/Notuploaded.jpg");
            }

            recipeService.addRecipe(recipe, typeString);
            redirectAttributes.addFlashAttribute("successMsg", "新增食譜成功！");
            return "redirect:/diy";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "新增失敗：請選擇正確的食譜分類！");
            return "redirect:/recipe/add";
        }
    }

    @GetMapping("recipe/edit/{id}")
    public String showEditRecipeForm(@PathVariable Integer id, HttpSession session, Model model,
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
            @PathVariable Integer id,
            @ModelAttribute Recipe updateRecipe, // 微調 1：補上 @ModelAttribute 讓 Spring 綁定更明確
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        Recipe existingRecipe = recipeService.findById(id);

        // 微調 2：防呆機制，萬一這個 ID 查不到食譜，直接擋下來
        if (existingRecipe == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "找不到該食譜，無法修改！");
            return "redirect:/diy";
        }

        // 修改時：如果有傳新照片，就覆蓋舊照片
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
                // 如果格式不對，掛上錯誤訊息，直接把使用者踢回 DIY 頁面，不呼叫 Service 存檔
                redirectAttributes.addFlashAttribute("errorMsg", "上傳失敗！僅支援 JPG, PNG, GIF, WEBP 格式的照片喔。");
                return "redirect:/diy";
            }

            fileStorageService.deleteOldImage(existingRecipe.getImageUrl());
            String newImageUrl = fileStorageService.saveUploadedImage(imageFile);
            if (newImageUrl != null) {
                updateRecipe.setImageUrl(newImageUrl);
            }
        } else {
            // 微調 3：直接在這裡把舊照片補回去！
            updateRecipe.setImageUrl(existingRecipe.getImageUrl());
        }

        // 微調 4：強迫把網址上的 ID 塞給物件，確保不會更新錯人或變成新增資料
        updateRecipe.setId(id);

        // 最後才安心交給 Service 去存檔
        recipeService.updateRecipe(updateRecipe, username);

        redirectAttributes.addFlashAttribute("successMsg", "食譜修改成功！");
        return "redirect:/diy";
    }

    // 攔截帶有 ID 的 GET 請求
    @GetMapping("/recipe/delete/{id}")
    public String deleteRecipeFallback(@PathVariable(required = false) Integer id,
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
    public String deleteRecipe(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        try {
            recipeService.deleteRecipe(id, username);
            redirectAttributes.addFlashAttribute("successMsg", "食譜已成功刪除！");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/diy";
    }
}