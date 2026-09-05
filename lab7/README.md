# Lab 7 - JSP/Servlet MVC

Mỗi thư mục `bai01` đến `bai13` là một Maven project độc lập. Các project web tạo file WAR để triển khai trên Tomcat 10.1+ (Jakarta Servlet 6).

## Build toàn bộ

```powershell
cd D:\Codespace\java\lab7
mvn clean package
```

## Chạy một bài

Mở thư mục bài trong IntelliJ/NetBeans, cấu hình Tomcat 10.1+, hoặc build rồi chép file trong `target/*.war` vào thư mục `webapps` của Tomcat.

Tài khoản mẫu cho bài 5 và bài 13: `admin` / `123456`.

## Luồng MVC

```text
Browser -> Servlet Controller -> Repository (List trong bộ nhớ)
        <- JSP View + JSTL <- request attributes
```

Các thao tác thêm/sửa/xóa dùng POST. Dữ liệu chỉ tồn tại trong RAM và được khởi tạo lại khi ứng dụng khởi động lại.
