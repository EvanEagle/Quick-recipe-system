package com.example.quick_recipe_system.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${app.image.recipe-directory}")
    private String recipeImageDirectory;

    // 因避免使用者上傳非圖片檔的資料,所以定義允許的圖片類型
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    public String saveUploadedImage(MultipartFile imageFile) {

        if (imageFile == null || imageFile.isEmpty()) {
            return null; // 沒上傳檔案就回傳 null
        }
        try {

            String originalFilename = imageFile.getOriginalFilename();

            if (originalFilename == null || originalFilename.isBlank()) {
                throw new IllegalArgumentException("無法取得上傳檔案名稱");
            }

            // 1. 精準切割主檔名與副檔名
            // 找出最後一個點點的位置
            int dotIndex = originalFilename.lastIndexOf(".");

            if (dotIndex <= 0 || dotIndex == originalFilename.length() - 1) {
                throw new IllegalArgumentException("圖片檔案名稱或副檔名不正確");
            }

            // 切出主檔名 (例如 "my_lunch.jpg" 切出 "my_lunch")
            String original = originalFilename.substring(0, dotIndex);

            // 切出副檔名 (例如 "my_lunch.jpg" 切出 ".jpg")
            String ext = originalFilename.substring(dotIndex);

            // 2. 建立時間戳記格式 (例如: 20260602_110815)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);

            // 3. 套用老師的命名公式：原檔名_時間_8碼亂數.副檔名
            String newFileName = original + "_" + timestamp + "_" +
                    UUID.randomUUID().toString().substring(0, 8) + ext;

            Path directory = Paths.get(recipeImageDirectory)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(directory);

            Path destination = directory
                    .resolve(newFileName)
                    .normalize();

            imageFile.transferTo(destination);

            return "/images/recipes/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("圖片儲存失敗", e);
        }
    }

    /**
     * 清除硬碟中的舊照片檔案
     */
    public void deleteOldImage(String imageUrl) {
        // 1. 如果沒有舊圖片，或者舊圖片是系統預設圖片，就絕對不要刪除！
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        /*
     * 只有 /images/recipes/ 底下的圖片才能刪除。
     *
     * 系統圖片例如：
     * /images/system/not-uploaded.jpg
     * /images/system/logo.png
     *
     * 都會直接略過。
     */
    String recipeUrlPrefix = "/images/recipes/";

    if (!imageUrl.startsWith(recipeUrlPrefix)) {
        return;
    }

    try {
        /*
         * /images/recipes/三杯雞.jpg
         * 取出：
         * 三杯雞.jpg
         */
        String fileName = imageUrl.substring(recipeUrlPrefix.length());

        Path recipeDirectory = Paths.get(recipeImageDirectory)
                .toAbsolutePath()
                .normalize();

        Path targetFile = recipeDirectory
                .resolve(fileName)
                .normalize();

        /*
         * 防止路徑跳脫。
         *
         * 例如惡意路徑：
         * /images/recipes/../../system/logo.png
         */
        if (!targetFile.startsWith(recipeDirectory)) {
            throw new SecurityException("不合法的圖片路徑：" + imageUrl);
        }

        boolean deleted = Files.deleteIfExists(targetFile);

        if (deleted) {
            System.out.println("舊食譜圖片刪除成功：" + targetFile);
        } else {
            System.out.println("找不到要刪除的舊圖片：" + targetFile);
        }

    } catch (IOException e) {
        /*
         * 暫時保留不中斷食譜修改流程的設計。
         */
        System.out.println("舊照片刪除失敗：" + e.getMessage());
    }
}
    /**
     * 處理圖片驗證與上傳
     */
    public void validateImage(MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String contentType = imageFile.getContentType();
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
                // 如果格式錯誤，直接拋出例外交給 Controller 處理
                throw new IllegalArgumentException("上傳失敗！僅支援 JPG, PNG, GIF, WEBP 格式的照片喔。");
            }
        }
    }
}
