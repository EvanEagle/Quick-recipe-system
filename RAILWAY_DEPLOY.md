# QuickCook Railway 部署筆記

## 架構
- Spring Boot：由 GitHub Repository 的 Dockerfile 建置
- MySQL：Railway MySQL Service
- 食譜圖片：Railway Volume 掛載到 `/app/images`

## App Service Variables
請將 Railway 內的 MySQL service 名稱假設為 `MySQL`，並在 Spring Boot service 設定：

```text
SPRING_PROFILES_ACTIVE=prod
DB_USERNAME=${{MySQL.MYSQLUSER}}
DB_PASSWORD=${{MySQL.MYSQLPASSWORD}}
SPRING_DATASOURCE_URL=jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?useSSL=false&serverTimezone=Asia/Taipei&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
APP_RECIPE_IMAGE_DIR=/app/images/recipes
```

`PORT` 由 Railway 提供時，Spring Boot 會自動讀取；未提供則預設 8080。

## Volume
在 Spring Boot service 新增 Railway Volume：

```text
Mount Path: /app/images
```

## Public Domain
Spring Boot service → Settings → Networking → Generate Domain。
因專案 context path 為 `/quick-cook`，首頁網址為：

```text
https://你的網域/quick-cook/home
```

## 舊資料搬移
新的 Railway MySQL 一開始沒有本機資料。如果要保留既有管理員、會員、食譜與收藏，需要先從本機 MySQL 匯出 `recipe_db`，再匯入 Railway MySQL。

食譜上傳圖片也要將本機 `images/recipes/` 的檔案放到 Railway Volume 的 `/app/images/recipes/`。

## 注意
- 不要把 `.env` commit 到 GitHub。
- 不要把資料庫密碼寫死在 `application-prod.yml`。
- `target/` 不需要上傳，Dockerfile 會在雲端自行 Maven build。
