package com.example.quick_recipe_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.service.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // admin/dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAllAttributes(adminService.getDashboardData());

        return "admin/dashboard";
    }

    // 會員管理
    @GetMapping("/members")
    public String showMembers(Model model) {
        model.addAttribute("members", adminService.getAllMembers());
        return "admin/members";
    }

    // 會員狀態切換
    @PostMapping("/members/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        adminService.toggleMemberStatus(id);
        return "redirect:/admin/members";
    }

    // 食譜管理
    @GetMapping("/recipes")
    public String showRecipes(Model model) {
        model.addAttribute("recipes", adminService.getAllRecipes());
        return "admin/recipes";
    }

    @GetMapping("/recipes/add")
    public String showAddRecipe(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "admin/recipe-add";
    }

    @PostMapping("/recipes/add")
    public String addOfficialRecipe(@RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session, @ModelAttribute Recipe recipe, String typeString,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");
        String role = (String) session.getAttribute("loggedInUserRole");

        try {
            adminService.addOfficialRecipe(recipe, typeString, imageFile, username, role);

            redirectAttributes.addFlashAttribute("successMsg", "官方食譜發布成功！");
            return "redirect:/admin/recipes"; // 成功則回到後台食譜列表

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/recipes/add"; // 失敗則回到新增官方食譜表單
        }
    }

    @PostMapping("/recipes/delete/{id}")
    public String deleteOfficialRecipe(@PathVariable Long id, HttpSession session,
            RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("loggedInUserRole");

        try {
            adminService.deleteOfficialRecipe(id, role);
            redirectAttributes.addFlashAttribute("successMsg", "已將食譜強制下架！");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/recipes";
    }

    @GetMapping("/recipes/edit/{id}")
    public String editOfficialRecipe(@PathVariable Long id, HttpSession session,
            Model model, RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("loggedInUserRole");

        try {
            Recipe recipe = adminService.getOfficialRecipeForEdit(id, role);

            model.addAttribute("recipe", recipe);

            return "recipe-edit";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "errorMsg",
                    e.getMessage());

            return "redirect:/admin/recipes";
        }
    }

    @PostMapping("/recipes/edit/{id}")
    public String editOfficialRecipe(@PathVariable Long id, HttpSession session, @ModelAttribute Recipe recipe,
            @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("loggedInUserRole");

        try {
            adminService.editOfficialRecipe(recipe, id, role, imageFile);

            redirectAttributes.addFlashAttribute("successMsg", "官方食譜編輯成功！");

            return "redirect:/admin/recipes";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());

            return "redirect:/admin/recipes/edit/" + id;
        }
    }
}
