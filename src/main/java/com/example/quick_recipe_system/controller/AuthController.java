package com.example.quick_recipe_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
//================= 登入 / 登出 =================
    /**
     * 1. 導向登入/註冊畫面
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * 2. 接收前端表單送來的帳號密碼進行登入
     */
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {

            User user = userService.login(username, password);

            session.setAttribute("loggedInUser", username);
            session.setAttribute("loggedInUserRole", user.getRole());

            // 管理員直接進後台，一般人去前台首頁
            if ("ROLE_ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/home";
            }

        } catch (IllegalArgumentException e) {
            // 捕捉到在 Service 拋出的錯誤訊息，傳回前端顯示
            model.addAttribute("errorMsg", e.getMessage());
            return "login";
        }
    }

    /** 
     * 3. 登出功能
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        /**
         * 本來用 session.removeAttribute() 來清除資料，
         * 但我後來審視程式碼發現 Session 連線通道依然有效，有資安隱患。
         * 因此我將它重構為直接呼叫 session.invalidate()。
         * 當登出請求進到 Controller 後，直接在伺服器端將整條 Session 徹底銷毀並釋放記憶體，確保連線最安全的關閉。」
         **/
        session.invalidate();

        return "redirect:/home"; // 登出後回到首頁
    }

//================== 註冊 =====================

    /**
     * 1. 導向註冊畫面
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * 2. 接收前端表單送來的帳號密碼進行註冊
     */
    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) { 
        try {
            userService.register(username, password); 

            // 註冊成功！讓redirectAttributes帶著成功訊息前往登入頁面
            redirectAttributes.addFlashAttribute("successMsg", "🎉 註冊成功！請使用新帳號登入。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "register";
    }
}
