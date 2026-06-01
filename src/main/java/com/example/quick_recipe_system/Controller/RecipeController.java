package com.example.quick_recipe_system.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe")
    public String exploreRecipes(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "cookingtime", required = false) String cookingtime,
            @RequestParam(value = "typeString", required = false) String typeString,
            Model model) {

        // 呼叫昨天與今天實作的綜合搜尋核心
        Map<String, List<Recipe>> typeRecipes = recipeService.masterSearch(keyword, cookingtime, null, typeString);

        model.addAttribute("typeRecipes", typeRecipes);

        // 用來讓探索頁面的搜尋欄能維持住選取的狀態
        model.addAttribute("selectedTime", cookingtime);
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("selectedType", typeString);

        return "recipe-list";
    }

    @GetMapping("/recipe/detail/{id}")
    public String showRecipeDetail(@PathVariable Integer id, Model model) {

        // 1. 透過 Service 去資料庫把這道食譜的完整資料撈出來
        // (假設你的 recipeService 裡面有 findById 這個方法)
        Recipe recipe = recipeService.findById(id);

        // 防呆機制：如果有人亂輸入網址 ID，找不到食譜，就直接踢回探索列表
        if (recipe == null) {
            return "redirect:/recipe";
        }

        // 2. 把撈出來的完整食譜資料裝進 Model，準備送給前端畫面
        model.addAttribute("recipe", recipe);

        // 3. 導向你剛剛建立好的詳情頁面 (注意名稱要跟你剛剛改的一致，不加 .html)
        return "recipe-list-detail";
    }

    @GetMapping("/diy")
    public String showDiyPage(HttpSession session, Model model) {
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }
        List<Recipe> myDiyRecipes = recipeService.findRecipesByAuthor(username);
        model.addAttribute("diyRecipes", myDiyRecipes);
        return "diy-page";
    }

    @GetMapping("/recipe/add")
    public String showAddRecipe(HttpSession session, Model model) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }
        model.addAttribute("recipe", new Recipe());
        return "recipe-add";
    }

    @PostMapping("/recipe/add")
    public String addRecipe(
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session,
            Recipe recipe,
            String typeString,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        try {
            recipe.setAuthor(username);

            // 呼叫輔助方法存圖片
            String imageUrl = saveUploadedImage(imageFile);

            if (imageUrl != null) {
                recipe.setImageUrl(imageUrl); // 有上傳，用新照片
            } else {
                // 🌟 使用者沒上傳，給予系統預設圖！
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

        if (username == null) {
            return "redirect:/login";
        }

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
            Recipe updateRecipe,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        // 修改時：如果有傳新照片，就覆蓋舊照片
        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = saveUploadedImage(imageFile);
            if (newImageUrl != null) {
                updateRecipe.setImageUrl(newImageUrl);
            }
        }
        // ⚠️ 注意：如果修改時沒傳新照片，請確保 recipeService.updateRecipe
        // 會自動去資料庫撈出舊的 imageUrl 補上去，否則照片會被清空喔！

        recipeService.updateRecipe(updateRecipe, username);

        redirectAttributes.addFlashAttribute("successMsg", "食譜修改成功！");
        return "redirect:/diy";
    }

    @PostMapping("/recipe/delete/{id}")
    public String deleteRecipe(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        try {
            recipeService.deleteRecipe(id, username);
            redirectAttributes.addFlashAttribute("successMsg", "食譜已成功刪除！");
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/diy";
    }

    /**
     * 儲存已上傳的圖片
     */
    private String saveUploadedImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null; // 沒上傳檔案就回傳 null
        }
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName = UUID.randomUUID().toString() + extension;

            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + "/src/main/resources/static/images/recipes/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(uploadDir + newFileName);
            imageFile.transferTo(dest);

            return "/images/recipes/" + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}