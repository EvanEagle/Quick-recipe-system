# 第一階段：在容器內編譯 Spring Boot 專案，雲端部署不需要預先提交 target/
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# 第二階段：只保留執行所需的 JRE 與 JAR，縮小正式映像
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN mkdir -p /app/images/recipes
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
