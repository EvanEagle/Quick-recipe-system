package com.example.quick_recipe_system.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.exception.NoLoggedInException;
import com.example.quick_recipe_system.exception.NoPermissionException;
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

        String username = (String) session.getAttribute("loggedInUser");

        // 如果根本沒登入，就拋出例外，隨後由 GlobalExceptionHandler 統一攔截這個例外
        if (username == null) {
            throw new NoLoggedInException("請先登入系統管理員才能使用此功能喔！");
        }

        User currentUser = userRepository.findByUsername(username);

        if (currentUser == null || !"ROLE_ADMIN".equals(currentUser.getRole())) {
            throw new NoPermissionException("您沒有權限使用此功能喔！");
        }
        return true;
    }
}
