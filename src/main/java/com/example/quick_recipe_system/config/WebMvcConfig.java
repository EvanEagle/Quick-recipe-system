package com.example.quick_recipe_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.quick_recipe_system.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 1. 設定要攔截的:加入所有需要登入的網址
                .addPathPatterns(
                        "/favorite/**", // 攔截所有收藏相關功能
                        "/diy/**", // 攔截 DIY 列表 (如果有這個路徑的話)
                        "/recipe/add", // 攔截新增食譜
                        "/recipe/edit/**", // 攔截修改食譜
                        "/recipe/delete/**" // 攔截刪除食譜
                )
                // 2. 設定要放行的「白名單」(通常不需要寫，但若有重疊可在此排除)
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/css/**",
                        "/images/**");
    }
}
