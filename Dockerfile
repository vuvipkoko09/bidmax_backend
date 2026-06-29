# Bước 1: Mượn môi trường Maven và JDK 21 để build code
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy toàn bộ code vào container
COPY . .
# Chạy lệnh đóng gói ra file .jar (bỏ qua test để tiết kiệm RAM)
RUN mvn clean package -DskipTests

# Bước 2: Tạo môi trường chạy siêu nhẹ với Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Nhặt file .jar vừa build xong ở Bước 1 sang đây
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 cho Render nhận diện
EXPOSE 8080

# Chạy server với lệnh ép giới hạn RAM tối đa 256MB
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]