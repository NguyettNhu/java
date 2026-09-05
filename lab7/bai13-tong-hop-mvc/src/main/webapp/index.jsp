<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lab 7 MVC</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
    </head>
    <body>
        <main class="shell">
            <div class="top">
                <div class="brand">EAUT · Lab 7 MVC</div>
                <a class="button" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </div>
            <section class="panel">
                <h1>Hệ thống quản lý tổng hợp</h1>
                <p>Ứng dụng JSP/Servlet theo mô hình MVC, dữ liệu lưu bằng List trong bộ nhớ.</p>
            </section>
            <br>
            <nav class="menu">
                <a class="card" href="${pageContext.request.contextPath}/admin/sinh-vien">
                    <h2>Sinh viên</h2>
                    <p>Thêm, sửa và xóa hồ sơ sinh viên.</p>
                </a>
                <a class="card" href="${pageContext.request.contextPath}/admin/sach">
                    <h2>Sách</h2>
                    <p>Quản lý danh mục sách và tác giả.</p>
                </a>
                <a class="card" href="${pageContext.request.contextPath}/admin/san-pham">
                    <h2>Sản phẩm</h2>
                    <p>Quản lý sản phẩm và giá bán.</p>
                </a>
            </nav>
        </main>
    </body>
</html>
