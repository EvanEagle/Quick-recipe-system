package com.example.quick_recipe_system.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    private Map<String, String> userDatabase = new HashMap<>();

    public UserService() {
        userDatabase.put("arden123", "a12345");
    }

    // 註冊功能
    public boolean register(String username, String password) throws IllegalArgumentException {
        // 1. 檢查帳號格式
        validateFormat(username, "帳號");
        // 2. 檢查密碼格式
        validateFormat(password, "密碼");
        
        if (userDatabase.containsKey(username)) {
            throw new IllegalArgumentException("此帳號已被註冊過囉！");
        }

        userDatabase.put(username, password);
        return true;
    }

    //檢查方法
    private void validateFormat(String input, String label) throws IllegalArgumentException {
        // 檢查長度是否 >= 5
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
    public void login(String username, String password) throws IllegalArgumentException {
        // 1. 先檢查帳號是否存在
        if (!userDatabase.containsKey(username)) {
            // 丟出一個特定的訊息，代表帳號沒找到
            throw new IllegalArgumentException("找不到此帳號，請先完成註冊！");
        }
        
        // 2. 帳號存在，再檢查密碼
        if (!userDatabase.get(username).equals(password)) {
            throw new IllegalArgumentException("密碼輸入錯誤，請重新確認。");
        }
    }
}
