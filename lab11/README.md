# Lab 11 - Spring Boot và Thymeleaf

Project hoàn thành hai yêu cầu:

1. Tạo ứng dụng Spring Boot có Spring Web, Thymeleaf và DevTools, chạy tại cổng `8080`.
2. Truy cập `/` để hiển thị trang chủ với dữ liệu được gửi từ `HomeController`.

## Chạy ứng dụng

Yêu cầu Java 21+ và Maven 3.9+.

```powershell
cd D:\Codespace\java\lab11
mvn spring-boot:run
```

Mở <http://localhost:8080>.

## Cấu trúc chính

```text
src/main/java/.../Lab11Application.java
src/main/java/.../controller/HomeController.java
src/main/resources/templates/home.html
src/main/resources/static/css/style.css
src/main/resources/application.properties
```

## Luồng dữ liệu

`HomeController` thêm dữ liệu vào `Model` và trả về tên view `home`.
Thymeleaf tìm file `templates/home.html`, đọc các biểu thức `th:text`, `th:each` và tạo HTML trả về trình duyệt.
