package com.example.quick_recipe_system.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.repository.RecipeRepository;
import com.example.quick_recipe_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true) // 唯讀事務，能優化資料庫查詢效能
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    private final RecipeRepository recipeRepository;

    /**
     * 彙整儀表板所需的所有營運數據
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> stats = new HashMap<>();

        // 1. 獲取總會員數
        long totalUsers = userRepository.count();

        // 2. 獲取總食譜數
        long totalRecipes = recipeRepository.count();

        // 3. 計算今日新增食譜數 (商業邏輯封裝在 Service)
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long newRecipesToday = recipeRepository.countByCreatedAtAfter(startOfToday);

        // 將所有數據打包
        stats.put("totalUsers", totalUsers);
        stats.put("totalRecipes", totalRecipes);
        stats.put("newRecipesToday", newRecipesToday);

        return stats;
    }

    // 會員管理-所有一般使用者
    public List<User> getAllMembers() {
        return userRepository.findByRole("ROLE_USER");
    }

    @Transactional
    public void toggleMemberStatus(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到此會員！"));

        // 狀態反轉：如果是 true 就變 false，反之亦然
        user.setIsActive(!user.getIsActive());

        userRepository.save(user);
    }
}
