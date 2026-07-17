package com.example.quick_recipe_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quick_recipe_system.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 用帳號去資料庫把使用者撈出來 (登入驗證用)
    User findByUsername(String username);
    
    // 檢查這個帳號是不是已經被註冊過了 (註冊防呆用)
    boolean existsByUsername(String username);

    // 只撈出所有角色為 ROLE_USER 的使用者 (管理員功能-會員管理用)
    List<User> findByRole(String role);
}