package com.example.quick_recipe_system.ui;

import java.util.InputMismatchException;
import java.util.Scanner;

public class InputHandler {

    private Scanner scanner;

    public InputHandler() {
        this.scanner = new Scanner(System.in);
    }

    public int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt); // 印出提示訊息
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // 清除換行符
                return value; // 成功拿到數字，直接回傳並結束方法
            } catch (InputMismatchException e) {
                System.out.println("【格式錯誤】請輸入數字，不要輸入文字！");
                scanner.nextLine(); // 重要：清理緩衝區的垃圾文字
            }
        }
    }
}
