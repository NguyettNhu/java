<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Đăng nhập</h1>
<p style="color:red">${error}</p>
<form action="${pageContext.request.contextPath}/login" method="post">
    <p>Tài khoản <input name="username" required>
    </p>
    <p>Mật khẩu <input type="password" name="password" required>
    </p>
    <button>Đăng nhập</button>
</form>
<p>Tài khoản mẫu: admin / 123456</p>
