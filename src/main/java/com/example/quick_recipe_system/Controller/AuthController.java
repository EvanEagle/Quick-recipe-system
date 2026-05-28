package com.example.quick_recipe_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quick_recipe_system.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    public final UserService userService;

    // 1. 導向登入/註冊畫面
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 2. 接收前端表單送來的帳號密碼進行登入
    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {
            // 呼叫 Service 檢查帳密
            userService.login(username, password);

            // 檢查通過！發放 VIP 手環 (Session)
            session.setAttribute("loggedInUser", username);

            // 登入成功，導向首頁
            return "redirect:/home";

        } catch (IllegalArgumentException e) {
            // 捕捉到你在 Service 拋出的錯誤訊息，傳回前端顯示
            model.addAttribute("errorMsg", e.getMessage());
            return "login"; // 停留在登入頁
        }
    }

    // 3. 登出功能
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 沒收 VIP 手環
        session.removeAttribute("loggedInUser");
        // 也可以用 session.invalidate(); 清除所有 Session 資料

        return "redirect:/home"; // 登出後回到首頁
    }

    // 1. 導向註冊畫面
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // 2. 接收前端表單送來的帳號密碼進行註冊
    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) { // redirectAttributes = 傳令兵
        try {
            userService.register(username, password); // 呼叫 UserService 的 register方法

            // 註冊成功！讓redirectAttributes帶著成功訊息前往登入頁面
            redirectAttributes.addFlashAttribute("successMsg", "🎉 註冊成功！請使用新帳號登入。");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMsg", e.getMessage());
        }
        return "register";
    }

}
