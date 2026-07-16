package com.example.quick_recipe_system.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 處理未登入例外
     */
    @ExceptionHandler(NoLoggedInException.class)
    public String handleNoLoggedInException(NoLoggedInException e, RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());

        return "redirect:/login";
    }

    /**
     * 當傳入的請求參數無法轉換為 Controller 方法中定義的預期 Java 資料類型時，TypeMismatchException就會發現此異常。
     **/
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatchException(RedirectAttributes redirectAttributes) {

        // 當使用者亂輸入非數字網址時, 攔截錯誤, 並重新導向探索食譜頁面
        redirectAttributes.addFlashAttribute("errorMsg", "系統提示：網址參數格式錯誤");
        return "redirect:/home";
    }

    /**
     * 處理無權限的例外
     */
    @ExceptionHandler(NoPermissionException.class)
    public String handleNoPermission(NoPermissionException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        return "redirect:/home";
    }
}
