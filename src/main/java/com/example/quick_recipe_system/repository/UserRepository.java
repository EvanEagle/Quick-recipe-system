package com.example.quick_recipe_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quick_recipe_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // 讓你可以用帳號去資料庫把這個人撈出來 (登入驗證用)
    User findByUsername(String username);
    
    // 檢查這個帳號是不是已經被註冊過了 (註冊防呆用)
    boolean existsByUsername(String username);
}