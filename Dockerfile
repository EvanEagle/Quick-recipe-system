# 使用輕量級的 Java 21 環境作為基底
FROM eclipse-temurin:21-jdk-alpine

# 設定容器內的工作目錄
WORKDIR /app

# 建立用來存放食譜圖片的資料夾
RUN mkdir -p /app/images

# 將編譯好的 jar 檔複製到容器內，並命名為 app.jar
COPY target/*.jar app.jar

# 宣告對外開放的 Port (這只是標示，實際要在 compose 設定)
EXPOSE 8080

# 啟動應用程式的指令
ENTRYPOINT ["java", "-jar", "app.jar"]