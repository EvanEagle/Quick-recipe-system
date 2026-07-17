package com.example.quick_recipe_system.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.quick_recipe_system.entity.User;
import com.example.quick_recipe_system.exception.UserStopException;
import com.example.quick_recipe_system.repository.UserRepository;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 讓 Lombok 自動幫我們注入 Repository
@Validated
public class UserService {

    // 嚴格遵守三層架構：Service 負責呼叫 Repository
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        //通過上面檢查程序後, 在寫入資料庫的前一刻，將密碼進行 BCrypt 加密
        String encodedPassword = passwordEncoder.encode(password);

        // 4. 存入真實的 MySQL 資料庫
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(encodedPassword);
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
    public User login(@NotBlank String username, @NotBlank String password) throws IllegalArgumentException {
        // 1. 先去資料庫把這個使用者撈出來
        User user = userRepository.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("帳號或密碼錯誤。如果您尚未加入，請先前往註冊喔！");
        }

        if (user.getIsActive()== null || !user.getIsActive()) {
            throw new UserStopException("您的帳號已被停權，請聯繫系統管理員！");
        }
        return user;
    }
}