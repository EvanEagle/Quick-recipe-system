package com.example.quick_recipe_system.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession();

        // 🌟 修正點 1：乖乖用 String 來接 Session 裡面的資料
        String username = (String) session.getAttribute("loggedInUser"); 

        // 如果根本沒登入，直接踢走
        if (username == null) {
            response.sendRedirect("/home"); 
            return false;
        }

        // 🌟 修正點 2：拿著字串去資料庫把真實的 User 物件找出來
        // (注意：這裡的 findByUsername 請換成你 UserRepository 裡實際查詢帳號的方法名稱)
        User currentUser = userRepository.findByUsername(username);

        // 核心防護邏輯：如果找不到人，或是角色「不是」管理員
        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect("/home"); 
            return false; 
        }

        // 身分確認無誤，放行！
        return true; 
    }
}
