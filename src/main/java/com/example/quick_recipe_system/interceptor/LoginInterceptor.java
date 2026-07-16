package com.example.quick_recipe_system.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.quick_recipe_system.exception.NoLoggedInException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    /**
     * 但因實作了介面 HandlerInterceptor 的 preHandle方法
     * 必須要有3個參數, 否則編譯器會跳出錯誤
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            //未登入狀態時直接拋出 NoLoggedInException , 隨後由GlobalExceptionHandler 統一攔截這個例外
            throw new NoLoggedInException("請先登入才能使用此功能喔！");
        }

        return true; 
    }
}