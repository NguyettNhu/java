<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản trị</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
    </head>
    <body>
        <main class="shell">
            <div class="top">
                <div class="brand">Quản trị · ${sessionScope.username}</div>
                <form action="${pageContext.request.contextPath}/logout" method="post">
                    <button>Đăng xuất</button>
                </form>
            </div>
            <nav class="menu">
                <a class="card" href="${pageContext.request.contextPath}/admin/sinh-vien">
                    <h2>Sinh viên</h2>
                </a>
                <a class="card" href="${pageContext.request.contextPath}/admin/sach">
                    <h2>Sách</h2>
                </a>
                <a class="card" href="${pageContext.request.contextPath}/admin/san-pham">
                    <h2>Sản phẩm</h2>
                </a>
            </nav>
        </main>
    </body>
</html>
