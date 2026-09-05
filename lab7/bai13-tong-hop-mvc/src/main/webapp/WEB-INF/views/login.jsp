<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đăng nhập</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
    </head>
    <body>
        <main class="shell">
            <section class="panel">
                <h1>Đăng nhập quản trị</h1>
                <p style="color:var(--danger)">${error}</p>
                <form method="post">
                    <label>Tài khoản</label>
                    <input name="username" required>
                    <label>Mật khẩu</label>
                    <input type="password" name="password" required>
                    <button>Đăng nhập</button>
                </form>
                <p>Tài khoản mẫu: <strong>admin / 123456</strong>
                </p>
            </section>
        </main>
    </body>
</html>
