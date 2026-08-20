package com.example.quick_recipe_system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.quick_recipe_system.entity.Recipe;
import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.repository.RecipeRepository;
import com.example.quick_recipe_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeManagementService recipeManagementService;

    /**
     * 彙整儀表板營運數據
     */
    public Map<String, Object> getDashboardData() {

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalRecipes", recipeRepository.count());

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        stats.put(
                "newRecipesToday",
                recipeRepository.countByCreatedAtAfter(startOfToday));

        return stats;
    }

    /**
     * 會員管理：取得所有一般會員
     */
    public List<User> getAllMembers() {
        return userRepository.findByRole("ROLE_USER");
    }

    /**
     * 啟用 / 停用會員
     */
    @Transactional
    public void toggleMemberStatus(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到此會員！"));

        user.setIsActive(!user.getIsActive());

        userRepository.save(user);
    }

    /**
     * 食譜管理：取得所有食譜
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAllByOrderByIdDesc();
    }

    /**
     * 新增官方食譜
     */
    public void addOfficialRecipe(Recipe recipe, String typeString, MultipartFile imageFile, String authorName,
            String role) {

        validateAdmin(role);
        recipe.setAuthor(authorName);
        recipe.setSystemRecipe(true);

        recipeManagementService.createRecipe(recipe, typeString, imageFile);
    }

    /**
     * 取得待修改的官方食譜
     */
    public Recipe getOfficialRecipeForEdit(Long id, String role) {

        validateAdmin(role);

        return getOfficialRecipe(id);
    }

    /**
     * 修改官方食譜
     */
    public void editOfficialRecipe(Recipe updatedRecipe, Long id, String role, MultipartFile imageFile) {

        validateAdmin(role);

        Recipe targetRecipe = getOfficialRecipe(id);

        recipeManagementService.updateRecipe(
                targetRecipe,
                updatedRecipe,
                imageFile);
    }

    /**
     * 管理食譜 : 刪除食譜 / 強制下架
     */
    public void deleteRecipeByAdmin(Long id, String role) {

        validateAdmin(role);

        Recipe targetRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("此食譜不存在！"));

        recipeManagementService.deleteRecipe(targetRecipe);
    }

    /**
     * 驗證管理員權限
     */
    private void validateAdmin(String role) {

        if (!"ROLE_ADMIN".equals(role)) {
            throw new IllegalArgumentException("您沒有管理員權限！");
        }
    }

    /**
     * 取得官方食譜
     */
    private Recipe getOfficialRecipe(Long id) {

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("此食譜不存在！"));

        if (!recipe.isSystemRecipe()) {
            throw new IllegalArgumentException("此食譜不是官方食譜！");
        }

        return recipe;
    }
}