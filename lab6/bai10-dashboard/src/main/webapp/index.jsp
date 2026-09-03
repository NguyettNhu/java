<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Lab 06 Student Web</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; line-height: 1.6; }
        a { color: #0f5fa8; text-decoration: none; }
        a:hover { text-decoration: underline; }
        .card { max-width: 720px; padding: 24px; border: 1px solid #d7dfe8; border-radius: 12px; background: #f9fbfd; }
    </style>
</head>
<body>
<div class="card">
    <h1>Lab 6 - Servlet, JSP, JSTL, Filter, Listener</h1>
    <p>Ung dung quan ly sinh vien co ban theo mo hinh MVC.</p>
    <ul>
        <li><a href="${pageContext.request.contextPath}/hello">Kiem tra HelloServlet</a></li>
        <li><a href="${pageContext.request.contextPath}/login">Dang nhap he thong</a></li>
        <li><a href="${pageContext.request.contextPath}/students">Danh sach sinh vien</a></li>
    </ul>
    <p>Tai khoan mau: <strong>admin</strong> / <strong>123456</strong></p>
</div>
</body>
</html>
