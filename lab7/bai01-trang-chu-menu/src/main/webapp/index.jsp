<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lab 7</title>
        <style>body{font-family:Segoe UI,sans-serif;max-width:900px;margin:60px auto;background:#f4f7fb;color:#17324d}nav{display:grid;grid-template-columns:repeat(2,1fr);gap:18px}a{padding:24px;background:white;border-left:5px solid #176b87;border-radius:8px;text-decoration:none;color:#17324d;box-shadow:0 5px 18px #17324d18}a:hover{transform:translateY(-2px)}</style>
    </head>
    <body>
        <h1>Hệ thống quản lý - Lab 7</h1>
        <p>Chọn module cần sử dụng:</p>
        <nav>
            <a href="${pageContext.request.contextPath}/sinh-vien">Quản lý sinh viên</a>
            <a href="${pageContext.request.contextPath}/sach">Quản lý sách</a>
            <a href="${pageContext.request.contextPath}/san-pham">Quản lý sản phẩm</a>
            <a href="${pageContext.request.contextPath}/login.jsp">Đăng nhập quản trị</a>
        </nav>
    </body>
</html>
