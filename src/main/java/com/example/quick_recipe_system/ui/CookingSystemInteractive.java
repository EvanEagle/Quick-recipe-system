package com.example.quick_recipe_system.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.example.quick_recipe_system.manager.*;
import com.example.quick_recipe_system.model.Recipe;
import com.example.quick_recipe_system.user.UserManager;

public class CookingSystemInteractive {
    private static Scanner scanner = new Scanner(System.in);
    private static RecipeRepository recipeRepository = new RecipeRepository();
    private static RecipeSearcher recipeSearcher = new RecipeSearcher();
    private static FavoriteManager favoriteManager = new FavoriteManager();
    private static InputHandler inputHandler = new InputHandler();
    private static UserManager userManager = new UserManager();

    public static void start() {

        boolean running = true;

        while (running) {

            String status = userManager.isLoggedIn() ? "已登入: " + userManager.getCurrentUser() : "未登入";
            System.out.println("\n[ 當前狀態: " + status + " ]");

            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║                 歡迎使用 快速料理小幫手                ║");
            System.out.println("║             Smart cooking search system                ║");
            System.out.println("║                 Interactive Version                    ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            System.out.println("\n1. 查看所有食譜");
            System.out.println("2. 依烹調時間搜尋食譜");
            System.out.println("3. 利用關鍵字搜尋食譜");
            System.out.println("4. 利用料理類型搜尋食譜");
            System.out.println("5. DIY食譜管理");
            System.out.println("6. 已收藏料理");
            System.out.println("7. 會員專區");
            System.out.println("8. 登出");
            System.out.println("0. 離開系統");

            int choice = inputHandler.getIntInput("請選擇功能:\n");

            switch (choice) {
                case 1:
                    showAllRecipes();
                    break;
                case 2:
                    showRecipesByCookingtime();
                    break;
                case 3:
                    showRecipesByKeyword();
                    break;
                case 4:
                    searchByCuisineType();
                    break;
                case 5:
                    if (checkLoginStatus())
                        DIYRecipesManage();
                    break;
                case 6:
                    if (checkLoginStatus())
                        showFavoriteRecipes();
                    break;
                case 7:
                    memberMenu();
                    break;
                case 8:
                    userManager.logout();
                    System.out.println("已登出。");
                    break;
                case 0:
                    running = false;
                    System.out.println("謝謝使用,再見! ");
                    break;
                default:
                    System.out.println("無效的選項！請重新輸入\n");
                    break;
            }
        }
    }

    private static boolean checkLoginStatus() {
        if (userManager.isLoggedIn()) {
            return true;
        } else {
            System.out.println("此功能僅限會員使用！");
            System.out.println("請先至「會員專區」進行登入。");
            return false;
        }
    }

    private static void memberMenu() {
        boolean backToMain = true;
        while (backToMain) {
            System.out.println("\n--- 會員專區 ---");
            System.out.println("1. 註冊新帳號");
            System.out.println("2. 會員登入");
            System.out.println("0. 返回主選單");

            int choice = inputHandler.getIntInput("請選擇:\n");
            switch (choice) {
                case 1:
                    handleRegister();
                    break;
                case 2:
                    handleLogin();
                    break;
                case 0:
                    backToMain = false;
                    break;
                default:
                    System.out.println("無效選項。");
            }
        }
    }

    private static void handleRegister() {
        boolean isSuccessful = false;

        System.out.println("\n--- 註冊新帳號 ---");
        System.out.println("[ 規範：帳號及密碼須包含英文+數字，且長度需 5 碼(含)以上 ]");

        while (!isSuccessful) {
            System.out.print("請輸入欲註冊的帳號 (輸入 0 返回): ");
            String user = scanner.nextLine();
            if (user.equals("0"))
                break; // 提供返回機制，避免使用者卡死

            System.out.print("請輸入欲設定的密碼: ");
            String pass = scanner.nextLine();

            try {
                // 呼叫 UserManager 的註冊邏輯
                if (userManager.register(user, pass)) {
                    System.out.println("註冊成功！現在您可以登入了。\n");
                    isSuccessful = true; // 註冊成功，跳出迴圈
                }
            } catch (IllegalArgumentException e) {
                // 攔截格式不符或帳號重複的錯誤
                System.out.println(e.getMessage()); // 印出「長度不足」或「須包含英文數字」
                System.out.println("請重新輸入！\n");
                // 因為 isSuccessful 還是 false，所以 while 會再次執行
            }
        }
    }

    private static void handleLogin() {
        if (userManager.isLoggedIn()) {
            System.out.println("您已經登入囉！");
            return;
        }
        System.out.print("帳號: ");
        String user = scanner.nextLine();
        System.out.print("密碼: ");
        String pass = scanner.nextLine();

        try {
            // 1. 直接呼叫。如果帳號密碼錯了，它會直接跳到 catch，不會往下跑
            userManager.login(user, pass);

            // 2. 如果沒被跳掉，代表登入成功
            System.out.println("歡迎回來，" + user + "！");

        } catch (IllegalArgumentException e) {
            // 3. 這裡會接到 UserManager 丟出來的「找不到帳號」或「密碼錯誤」
            System.out.println(e.getMessage());
        }
    }

    private static void showRecipeList(List<Recipe> recipes, boolean includeTime) {
        if (recipes == null || recipes.isEmpty()) {
            System.out.println("找不到符合條件的食譜,返回主選單...\n");
            return;
        }
        System.out.println("=".repeat(30));
        for (int i = 0; i < recipes.size(); i++) {
            Recipe r = recipes.get(i);

            String timeTag = includeTime ? "( " + r.getCookingTime() + " 分鐘 )" : "";

            System.out.println(i + 1 + ". " + r.getName() + timeTag);
        }
        System.out.println("=".repeat(30));
    }

    // 1. 查看所有食譜功能
    private static void showAllRecipes() {
        System.out.println("\n你選擇「查看所有食譜」");
        List<Recipe> recipes = recipeRepository.getAllRecipes();
        showRecipeList(recipes, false);

        if (recipes.isEmpty()) {
            return;
        }

        int recipeChoice = inputHandler.getIntInput("請選擇想查看食譜編號:\n");

        if (recipeChoice < 1 || recipeChoice > recipes.size()) {
            System.out.println("選擇錯誤,返回主選單...\n");
            return;
        }
        Recipe selectRecipe = recipes.get(recipeChoice - 1);
        System.out.println("\n食譜內容: ");
        System.out.println(selectRecipe);

        if (!userManager.isLoggedIn()) {
            System.out.println("登入後即可收藏此食譜！");
            return;
        }

        boolean stayInYesOrNo = true;

        while (stayInYesOrNo) {
            System.out.println("是否收藏食譜？");
            System.out.println("1.是\n2.否\n");

            int input = inputHandler.getIntInput("請選擇:\n");
            switch (input) {
                case 1:
                    favoriteManager.addFavoriteRecipes(selectRecipe);
                    stayInYesOrNo = false;
                    break;
                case 2:
                    System.out.println("取消收藏,返回主選單...\n");
                    stayInYesOrNo = false;
                    return;
                default:
                    System.out.println("錯誤選項,只能輸入 1 或 2 ");
                    break;
            }
        }
    }

    // 2. 依烹調時間搜尋食譜功能
    private static void showRecipesByCookingtime() {
        System.out.println("\n你選擇「依烹調時間搜尋食譜」");

        int time = inputHandler.getIntInput("請輸入可用時間(分鐘):\n");
        List<Recipe> recipes = recipeSearcher.searchByCookingTime(recipeRepository.getAllRecipes(), time);
        showRecipeList(recipes, true);

        if (recipes.isEmpty()) {
            return;
        }

        int recipeChoice = inputHandler.getIntInput("\n請選擇想查看食譜編號:\n");

        if (recipeChoice < 1 || recipeChoice > recipes.size()) {
            System.out.println("選擇錯誤,返回主選單...\n");
            return;
        }

        Recipe selectRecipe = recipes.get(recipeChoice - 1);
        System.out.println("\n食譜內容: ");
        System.out.println(selectRecipe);

        if (!userManager.isLoggedIn()) {
            System.out.println("登入後即可收藏此食譜！");
            return;
        }

        boolean stayInYesOrNo = true;

        while (stayInYesOrNo) {
            System.out.println("是否收藏食譜？");
            System.out.println("1.是\n2.否\n");

            int input = inputHandler.getIntInput("請選擇:\n");
            switch (input) {
                case 1:
                    favoriteManager.addFavoriteRecipes(selectRecipe);
                    stayInYesOrNo = false;
                    break;
                case 2:
                    System.out.println("取消收藏,返回主選單...\n");
                    stayInYesOrNo = false;
                    return;
                default:
                    System.out.println("錯誤選項,只能輸入 1 或 2 ");
                    break;
            }
        }
    }

    // 3. 利用關鍵字搜尋食譜功能
    private static void showRecipesByKeyword() {
        System.out.println("\n你選擇「利用關鍵字搜尋食譜」");
        System.out.println("請輸入關鍵字: ");
        String keyword = scanner.nextLine();
        List<Recipe> recipes = recipeSearcher.searchByKeyword(recipeRepository.getAllRecipes(), keyword);
        showRecipeList(recipes, false);

        if (recipes.isEmpty()) {
            return;
        }

        System.out.println("\n請選擇想查看食譜編號: ");
        int recipeChoice = scanner.nextInt();
        scanner.nextLine();

        if (recipeChoice < 1 || recipeChoice > recipes.size()) {
            System.out.println("選擇錯誤,返回主選單...\n");
            return;
        }
        Recipe selectRecipe = recipes.get(recipeChoice - 1);
        System.out.println("\n食譜內容: ");
        System.out.println(selectRecipe);

        if (!userManager.isLoggedIn()) {
            System.out.println("登入後即可收藏此食譜！");
            return;
        }

        boolean stayInYesOrNo = true;

        while (stayInYesOrNo) {
            System.out.println("是否收藏食譜？");
            System.out.println("1.是\n2.否\n");

            int input = inputHandler.getIntInput("請選擇:\n");
            switch (input) {
                case 1:
                    favoriteManager.addFavoriteRecipes(selectRecipe);
                    stayInYesOrNo = false;
                    break;
                case 2:
                    System.out.println("取消收藏,返回主選單...\n");
                    stayInYesOrNo = false;
                    return;
                default:
                    System.out.println("錯誤選項,只能輸入 1 或 2 ");
                    break;
            }
        }
    }

    // 4. 利用料理類型搜尋食譜功能
    private static void searchByCuisineType() {

        System.out.println("\n你選擇「利用料理類型搜尋食譜」");
        System.out.println("1. 中式料理");
        System.out.println("2. 日式料理");
        System.out.println("3. 西式料理");
        System.out.println("0. 返回主選單");
        int choice = inputHandler.getIntInput("請選擇:\n");

        String selectedType;

        switch (choice) {
            case 1:
                System.out.println("\n你選擇的是「中式料理」");
                selectedType = "中式料理";
                break;
            case 2:
                System.out.println("\n你選擇的是「日式料理」");
                selectedType = "日式料理";
                break;
            case 3:
                System.out.println("\n你選擇的是「西式料理」");
                selectedType = "西式料理";
                break;
            case 0:
                System.out.println("返回主選單...\n");
                return;
            default:
                System.out.println("無效的選項！ 請重新輸入\n");
                return;
        }

        List<Recipe> recipes = recipeSearcher.searchByCuisineType(recipeRepository.getCuisineMap(), selectedType);

        if (recipes.isEmpty()) {
            return;
        }

        int recipeChoice = inputHandler.getIntInput("\n請選擇想查看食譜編號:\n");

        if (recipeChoice < 1 || recipeChoice > recipes.size()) {
            System.out.println("選擇錯誤");
            return;
        }
        Recipe selectRecipe = recipes.get(recipeChoice - 1);
        System.out.println("\n食譜內容: ");
        System.out.println(selectRecipe);

        if (!userManager.isLoggedIn()) {
            System.out.println("登入後即可收藏此食譜！");
            return;
        }

        boolean stayInYesOrNo = true;

        while (stayInYesOrNo) {
            System.out.println("是否收藏食譜？");
            System.out.println("1.是\n2.否\n");

            int input = inputHandler.getIntInput("請選擇:\n");
            switch (input) {
                case 1:
                    favoriteManager.addFavoriteRecipes(selectRecipe);
                    stayInYesOrNo = false;
                    break;
                case 2:
                    System.out.println("取消收藏,返回主選單...\n");
                    stayInYesOrNo = false;
                    return;
                default:
                    System.out.println("錯誤選項,只能輸入 1 或 2 ");
                    break;
            }
        }
    }

    private static void DIYRecipesManage() {
        System.out.println("\n你選擇的是「DIY食譜管理」");
        System.out.println("1. 新增食譜");
        System.out.println("2. 刪除食譜");
        System.out.println("3. 修改食譜");
        System.out.println("0. 返回主選單");
        int choice = inputHandler.getIntInput("請選擇操作:\n");

        switch (choice) {
            case 1:
                createRecipe();
                break;
            case 2:
                removeRecipe();
                break;
            case 3:
                updateRecipe();
                break;
            case 0:
                System.out.println("返回主選單...\n");
                return;
            default:
                System.out.println("無效的選項！請重新輸入\n");
                break;
        }
    }

    // 5.食譜管理內的新增食譜方法:
    private static void createRecipe() {

        System.out.println("\n你選擇的是「新增食譜」");
        System.out.println("請輸入料理名稱: ");
        String nameInput = scanner.nextLine();

        int cookingTimeInput = inputHandler.getIntInput("\n請輸入烹調時間(分鐘):\n");

        System.out.println("\n請輸入主食材(用逗號分隔): ");
        String ingredientsInput = scanner.nextLine();
        List<String> ingredients = Arrays.asList(ingredientsInput.split(","));

        System.out.println("\n請輸入調味料(用逗號分隔): ");
        String seasoningsInput = scanner.nextLine();
        List<String> seasonings = Arrays.asList(seasoningsInput.split(","));

        System.out.println("\n請輸入作法(用逗號分隔): ");
        String stepsInput = scanner.nextLine();
        List<String> steps = Arrays.asList(stepsInput.split(","));

        System.out.println("\n請輸入關鍵字(用逗號分隔): ");
        String keywordsInput = scanner.nextLine();
        List<String> keywords = Arrays.asList(keywordsInput.split(","));

        System.out.println("\n存入哪一個料理類型: ");
        System.out.println("1. 中式料理");
        System.out.println("2. 日式料理");
        System.out.println("3. 西式料理");
        int choice = inputHandler.getIntInput("請選擇:\n");

        String typeInput;

        switch (choice) {
            case 1:
                typeInput = "中式料理";
                break;
            case 2:
                typeInput = "日式料理";
                break;
            case 3:
                typeInput = "西式料理";
                break;
            default:
                System.out.println("無效的選項！請重新輸入\n");
                return;
        }
        Recipe recipe = new Recipe(nameInput, cookingTimeInput, ingredients, seasonings, steps, keywords);
        recipeRepository.addRecipeToCuisine(typeInput, recipe);
        System.out.println("已將 " + recipe.getName() + " 新增至 「" + typeInput + "」");
    }

    // 5.食譜管理內的刪除食譜方法:
    private static void removeRecipe() {
        System.out.println("\n你選擇的是「刪除食譜」");
        System.out.println("你想刪除的料理是哪一種類型呢？ ");
        System.out.println("1. 中式料理");
        System.out.println("2. 日式料理");
        System.out.println("3. 西式料理");
        int choice = inputHandler.getIntInput("請選擇:\n");

        switch (choice) {
            case 1:
                processDeleteAction("中式料理");
                break;
            case 2:
                processDeleteAction("日式料理");
                break;
            case 3:
                processDeleteAction("西式料理");
                break;
            default:
                System.out.println("無效的選項！請重新輸入\n");
                return;
        }
    }

    // 刪除料理的刪除操作:
    private static void processDeleteAction(String type) {
        recipeRepository.showRecipesByCuisine(type);

        int id = inputHandler.getIntInput("請選擇要刪除的食譜編號:\n");

        boolean stayInYesOrNo = true;

        while (stayInYesOrNo) {
            System.out.println("提示：確定要刪除食譜？");
            System.out.println("1.是\n2.否\n");

            int input = inputHandler.getIntInput("請選擇: \n");
            switch (input) {
                case 1:
                    recipeRepository.removeRecipe(type, id);
                    stayInYesOrNo = false;
                    break;
                case 2:
                    System.out.println("取消刪除,返回主選單...\n");
                    stayInYesOrNo = false;
                    return;
                default:
                    System.out.println("錯誤選項,只能輸入 1 或 2 ");
                    break;
            }
        }
    }

    // 5.食譜管理內的修改食譜方法:
    private static void updateRecipe() {
        System.out.println("\n你選擇的是「修改食譜」");
        System.out.println("你想修改的料理是哪一種類型呢？");
        System.out.println("1. 中式料理");
        System.out.println("2. 日式料理");
        System.out.println("3. 西式料理");
        int choice = inputHandler.getIntInput("請選擇:\n");

        switch (choice) {
            case 1:
                processUpdateAction("中式料理");
                break;
            case 2:
                processUpdateAction("日式料理");
                break;
            case 3:
                processUpdateAction("西式料理");
                break;
            default:
                System.out.println("無效的選項！請重新輸入\n");
                break;
        }
    }

    private static void processUpdateAction(String type) {
        System.out.println("\n請選擇要修改的食譜編號");
        recipeRepository.showRecipesByCuisine(type);
        int id = inputHandler.getIntInput("請選擇:\n");

        boolean stayInYesOrNo = true;

        while (stayInYesOrNo) {
            System.out.println("提示：確定要修改食譜？");
            System.out.println("1.是\n2.否\n");

            int input = inputHandler.getIntInput("請選擇: ");
            switch (input) {
                case 1:
                    stayInYesOrNo = false;
                    break;
                case 2:
                    System.out.println("取消修改,返回主選單...\n");
                    stayInYesOrNo = false;
                    return;
                default:
                    System.out.println("錯誤選項,只能輸入 1 或 2 ");
                    break;
            }
        }

        Recipe target = recipeRepository.getRecipe(type, id);
        if (target == null) {
            System.out.println("找不到該食譜編號！");
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n當前食譜資訊: "); // 把當前食譜資訊擺在前面,每次修改後都可以顯示食譜
            System.out.println(target.toString());

            System.out.println("\n請問你想修改食譜的哪一個項目？");
            System.out.println("1. 修改食譜名稱");
            System.out.println("2. 修改烹調時間");
            System.out.println("3. 修改食材");
            System.out.println("4. 修改調味料");
            System.out.println("5. 修改作法");
            System.out.println("6. 修改關鍵字");
            System.out.println("0. 返回主選單");
            int choice = inputHandler.getIntInput("請選擇:\n");

            switch (choice) {
                case 1:
                    System.out.println("\n你選擇修改食譜名稱");
                    System.out.println("輸入新名稱: ");
                    String newName = scanner.nextLine();
                    target.setName(newName);
                    System.out.println("已成功修改食譜名稱");
                    break;
                case 2:
                    System.out.println("\n你選擇修改烹調時間");
                    System.out.println("");
                    int newTime = inputHandler.getIntInput("輸入更新的時間(分鐘):\n");
                    target.setCookingTime(newTime);
                    System.out.println("已成功修改烹調時間");
                    break;
                case 3:
                    System.out.println("\n你選擇修改食材");
                    System.out.println("輸入更新的食材(用逗號分隔): ");
                    String newIngredients = scanner.nextLine();
                    List<String> ingredients = Arrays.asList(newIngredients.split(","));
                    target.setIngredients(ingredients);
                    System.out.println("已成功修改食材");
                    break;
                case 4:
                    System.out.println("\n你選擇修改調味料");
                    System.out.println("輸入更新的調味料(用逗號分隔): ");
                    String newSeasonings = scanner.nextLine();
                    List<String> seasonings = Arrays.asList(newSeasonings.split(","));
                    target.setSeasonings(seasonings);
                    System.out.println("已成功修改調味料\n");
                    break;
                case 5:
                    System.out.println("\n你選擇修改作法");
                    System.out.println("輸入更新的作法(用逗號分隔): ");
                    String newStep = scanner.nextLine();
                    List<String> steps = Arrays.asList(newStep.split(","));
                    target.setSteps(steps);
                    System.out.println("已成功修改作法");
                    break;
                case 6:
                    System.out.println("\n你選擇修改關鍵字");
                    System.out.println("輸入更新的關鍵字(用逗號分隔): ");
                    String newKeywords = scanner.nextLine();
                    List<String> keywords = Arrays.asList(newKeywords.split(","));
                    target.setKeywords(keywords);
                    System.out.println("已成功修改關鍵字");
                    break;
                case 0:
                    System.out.println("\n返回主選單...");
                    running = false;
                    break;
                default:
                    System.out.println("無效的選項！請重新輸入\n");
                    break;
            }
        }
    }

    private static void showFavoriteRecipes() {

        List<Recipe> myfavorites = favoriteManager.getFavoriteRecipes();
        List<Recipe> allRecipes = recipeRepository.getAllRecipes();

        if (myfavorites.isEmpty()) {
            System.out.println("你的收藏清單空空如也，趕快去蒐集美食吧！\n");
            return;
        }

        boolean stayInFavorite = true;

        while (stayInFavorite) {
            System.out.println("\n我的收藏食譜清單 :");
            System.out.println("=".repeat(30));
            for (int i = 0; i < myfavorites.size(); i++) {
                Recipe fav = myfavorites.get(i);

                // 檢查這道收藏的菜，是否還在總倉庫裡
                if (allRecipes.contains(fav)) {
                    System.out.println((i + 1) + ". " + fav.getName());
                } else {
                    // 如果倉庫裡找不到了，就加上標籤
                    System.out.println((i + 1) + ". " + fav.getName() + " (原食譜已被刪除)");
                }
            }
            System.out.println("=".repeat(30));

            System.out.println("\n請選擇操作：");
            System.out.println("1.查看食譜\n2.移除收藏\n0.返回主選單");
            int choice = inputHandler.getIntInput("請選擇：\n");

            switch (choice) {
                case 1:
                    int showId = inputHandler.getIntInput("請選擇想查看食譜編號:\n");

                    if (showId > 0 && showId <= myfavorites.size()) {
                        System.out.println("食譜內容: ");
                        System.out.println(myfavorites.get(showId - 1).toString());
                        System.out.println("\n 閱讀完畢,即將返回主選單...\n");
                        stayInFavorite = false;
                        break;
                    } else {
                        System.out.println("選擇錯誤,請重新選擇: ");
                        break;
                    }
                case 2:
                    int id = inputHandler.getIntInput("請選擇要移除的食譜編號:\n");

                    boolean stayInYesOrNo = true;

                    while (stayInYesOrNo) {
                        System.out.println("提示：確定要移除食譜？");
                        System.out.println("1.是\n2.否\n");

                        int input = inputHandler.getIntInput("請選擇:\n");
                        switch (input) {
                            case 1:
                                favoriteManager.removeFavorite(id);
                                stayInYesOrNo = false;
                                break;
                            case 2:
                                System.out.println("取消移除,返回主選單...\n");
                                stayInYesOrNo = false;
                                return;
                            default:
                                System.out.println("錯誤選項,只能輸入 1 或 2 ");
                                break;
                        }
                    }

                    if (myfavorites.isEmpty()) {
                        System.out.println("收藏清單已空,返回主選單...\n");
                        stayInFavorite = false;
                    }
                    break;
                case 0:
                    System.out.println("返回主選單...\n");
                    stayInFavorite = false;
                    break;
                default:
                    System.out.println("無效的選項！ 請重新輸入");
                    break;
            }
        }
    }
}
