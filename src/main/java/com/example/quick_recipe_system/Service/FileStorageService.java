package com.example.quick_recipe_system.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${upload.path}")
    private String uploadPath;

    // 因避免使用者上傳非圖片檔的資料,所以定義允許的圖片類型
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

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

            // 4.確保資料夾存在 (例如 yml 設定的 /app/images/)
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 5. 組合完整的存檔路徑 (例如：/app/images/my_lunch_20260602_110815_a1b2c3d4.jpg)
            File dest = new File(uploadPath + newFileName);

            // 6. 真正執行存檔動作
            imageFile.transferTo(dest);

            // 注意：因為WebConfig 設定是攔截 "/images/**"
            // 7. 所以只要回傳 "/images/" 加上檔名就可以了。
            return "/images/" + newFileName;

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
