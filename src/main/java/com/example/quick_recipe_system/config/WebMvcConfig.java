package com.example.quick_recipe_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.quick_recipe_system.interceptor.AdminInterceptor;
import com.example.quick_recipe_system.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

        @Value("${app.image.recipe-directory}")
        private String recipeImageDirectory;

        private final LoginInterceptor loginInterceptor;
        private final AdminInterceptor adminInterceptor;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(); // 預設強度為 10，防禦力與效能的最佳平衡
        }

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

                registry.addInterceptor(adminInterceptor)
                                .addPathPatterns("/admin/**");
        }

        // 只有「使用者上傳的食譜圖片」走外部檔案系統。
        // logo、預設圖、使用者圖示等系統圖片仍由 classpath:/static/images/ 提供，
        // 避免雲端掛載空白 Volume 後把系統圖片一起遮掉。
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                String location = recipeImageDirectory.endsWith("/")
                                ? recipeImageDirectory
                                : recipeImageDirectory + "/";

                registry.addResourceHandler("/images/recipes/**")
                                .addResourceLocations("file:" + location);
        }
}
