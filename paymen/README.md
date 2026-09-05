# Paymen - Form thanh toán Maven

Project minh họa form thanh toán bằng JSP, Jakarta Servlet và Maven. Đây là giao dịch mô phỏng, không kết nối cổng thanh toán thật và không thu thập số thẻ.

## Build

```powershell
cd D:\Codespace\java\paymen
mvn clean package
```

Triển khai file `target/paymen.war` trên Tomcat 10.1+, sau đó mở:

```text
http://localhost:8080/paymen/
```

Trên máy hiện tại có thể build và chạy bằng:

```powershell
mvn package
.\run-tomcat.bat
```
