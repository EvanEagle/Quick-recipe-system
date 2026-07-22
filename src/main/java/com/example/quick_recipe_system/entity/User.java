package com.example.quick_recipe_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "users") // 指定資料表名稱
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // 帳號必填且全系統唯一，防止重複註冊
    @NotBlank
    private String username;

    @Column(nullable = false)
    @NotBlank
    private String password;
    
    // 角色權限：預設註冊進來都是一般用戶
    @Column(nullable = false)
    private String role = "ROLE_USER"; 

    // 帳號狀態：預設為 true (正常啟用)，若改為 false 則代表被停權
    @Column(nullable = false)
    private Boolean isActive = true;
}
