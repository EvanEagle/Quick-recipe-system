package com.example.quick_recipe_system.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 覆寫 preHandle：在請求到達 Controller 之前執行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            //  1. 建立 FlashMap (這就是 RedirectAttributes 的底層真實身分)
            FlashMap flashMap = new FlashMap();
            // 放入想要顯示的錯誤訊息 (Key 必須跟 HTML 裡接收的名稱一模一樣)
            flashMap.put("errorMsg", "請先登入才能使用此功能喔！");

            // 2. 呼叫 Spring 的大管家，把這個 Map 存起來，保證跳轉後還活著
            FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
            if (flashMapManager != null) {
                flashMapManager.saveOutputFlashMap(flashMap, request, response);
            }

            // 3. 執行重定向並踢回登入頁面
            String contextPath = request.getContextPath();
            response.sendRedirect(contextPath + "/login");
            
            return false;
        }

        return true;
    }
}