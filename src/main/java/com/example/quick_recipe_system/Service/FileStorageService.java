package com.example.quick_recipe_system.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    public String saveUploadedImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null; // 沒上傳檔案就回傳 null
        }
        try {
            String originalFilename = imageFile.getOriginalFilename();

            // 1. 精準切割主檔名與副檔名
            // 找出最後一個點點的位置
            int dotIndex = originalFilename.lastIndexOf(".");
            // 切出主檔名 (例如 "my_lunch.jpg" 切出 "my_lunch")
            String original = originalFilename.substring(0, dotIndex);
            // 切出副檔名 (例如 "my_lunch.jpg" 切出 ".jpg")
            String ext = originalFilename.substring(dotIndex);

            // 2. 建立時間戳記格式 (例如: 20260602_110815)
            DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(FMT);

            // 3. 套用老師的命名公式：原檔名_時間_8碼亂數.副檔名
            String newFileName = original + "_" + timestamp + "_" +
                    UUID.randomUUID().toString().substring(0, 8) + ext;

            // 4. 準備寫入硬碟的路徑邏輯 (維持你原本正確的寫法)
            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + "/src/main/resources/static/images/recipes/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File dest = new File(uploadDir + newFileName);
            imageFile.transferTo(dest);

            return "/images/recipes/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 清除硬碟中的舊照片檔案
     */
    public void deleteOldImage(String imageUrl) {
        // 1. 如果沒有舊圖片，或者舊圖片是系統預設圖片 (例如 /images/Notuploaded.jpg)，就絕對不要刪除！
        if (imageUrl == null || imageUrl.isEmpty() || imageUrl.equals("/images/Notuploaded.jpg")) {
            return;
        }

        try {
            // 2. 組合出舊檔案在硬碟中的絕對路徑
            String projectPath = System.getProperty("user.dir");
            // 注意：imageUrl 本身已經帶有 "/images/..."，所以直接拼接即可
            String filePath = projectPath + "/src/main/resources/static" + imageUrl;

            // 3. 找到檔案並執行刪除
            File oldFile = new File(filePath);
            if (oldFile.exists()) {
                oldFile.delete(); // 真正把硬碟裡的檔案刪除的關鍵指令
            }
        } catch (Exception e) {
            // 如果刪除失敗，印出錯誤訊息，但不中斷整個修改食譜的流程
            System.out.println("舊照片刪除失敗：" + e.getMessage());
        }
    }
}
