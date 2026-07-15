package com.example.quick_recipe_system.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.repository.UserRepository;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 讓 Lombok 自動幫我們注入 Repository
@Validated
public class UserService {

    // 嚴格遵守三層架構：Service 負責呼叫 Repository
    private final UserRepository userRepository;

    // 註冊功能
    public boolean register(@NotBlank String username, @NotBlank String password) throws IllegalArgumentException {
        // 1. 檢查帳號格式
        validateFormat(username, "帳號");
        // 2. 檢查密碼格式
        validateFormat(password, "密碼");

        // 3. 改成去資料庫查詢帳號是否重複
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("此帳號已被註冊過囉！");
        }

        // 4. 存入真實的 MySQL 資料庫
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        userRepository.save(newUser);

        return true;
    }

    // 檢查方法
    private void validateFormat(String input, String label) throws IllegalArgumentException {

        if (input == null || input.length() < 5) {
            throw new IllegalArgumentException(label + "長度不足！需要 5 碼以上！");
        }
        // 檢查是否包含英文與數字 (使用正規表示法)
        // ^(?=.*[A-Za-z])(?=.*\d) 代表必須包含字母與數字
        if (!input.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
            throw new IllegalArgumentException(label + "格式錯誤！必須包含英文與數字！");
        }
    }

    // 登入邏輯
    public void login(@NotBlank String username, @NotBlank String password) throws IllegalArgumentException {
        // 1. 先去資料庫把這個使用者撈出來
        User user = userRepository.findByUsername(username);

        // 2. 檢查使用者輸入的帳號是否存在
        if (user == null) {
            // 如果不存在，拋出:輸入參數不合法異常，並顯示提示
            throw new IllegalArgumentException("找不到此帳號，請先完成註冊！");
        }

        // 3. 帳號存在，核對使用者輸入的密碼是否與資料庫裡的密碼相符
        if (!user.getPassword().equals(password)) {
            // 如果不相符，拋出:輸入參數不合法異常，並顯示提示
            throw new IllegalArgumentException("密碼輸入錯誤，請重新確認。");
        }
    }
}