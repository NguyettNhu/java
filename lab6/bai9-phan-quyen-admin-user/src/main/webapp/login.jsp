<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dang nhap</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f3f7fb; }
        .panel { max-width: 420px; padding: 24px; background: #fff; border-radius: 12px; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08); }
        input { width: 100%; padding: 10px; margin: 8px 0 16px; box-sizing: border-box; }
        button { padding: 10px 16px; border: 0; border-radius: 8px; background: #0f5fa8; color: #fff; cursor: pointer; }
        .error { color: #c62828; }
    </style>
</head>
<body>
<div class="panel">
    <h2>Dang nhap he thong</h2>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <label>Ten dang nhap:</label>
        <input type="text" name="username" placeholder="Nhap username">

        <label>Mat khau:</label>
        <input type="password" name="password" placeholder="Nhap password">

        <button type="submit">Dang nhap</button>
    </form>

    <p class="error">${error}</p>
    <p>Tai khoan Admin: admin / 123456</p>
    <p>Tai khoan User: user / 123456</p>
    <p><a href="${pageContext.request.contextPath}/">Ve trang chu</a></p>
</div>
</body>
</html>
