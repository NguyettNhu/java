<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard sau dang nhap</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 36px; background: #f5f8fb; }
        .grid { display: grid; gap: 18px; max-width: 900px; }
        .card { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08); }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 10px; border: 1px solid #d7dfe8; text-align: left; }
        th { background: #eef3f8; }
        a { color: #0f5fa8; text-decoration: none; margin-right: 16px; }
    </style>
</head>
<body>
<div class="grid">
    <div class="card">
        <h2>Xin chao, ${sessionScope.username}</h2>
        <p>Thoi gian dang nhap: ${sessionScope.loginTime}</p>
        <p>Tong so sinh vien: <strong>${totalStudents}</strong></p>
        <p>
            <a href="${pageContext.request.contextPath}/students">Quan ly sinh vien</a>
            <a href="${pageContext.request.contextPath}/hello">HelloServlet</a>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </p>
    </div>

    <div class="card">
        <h3>Thong ke sinh vien theo lop</h3>
        <table>
            <tr>
                <th>Lop</th>
                <th>So luong</th>
            </tr>
            <c:choose>
                <c:when test="${empty classCounts}">
                    <tr>
                        <td colspan="2">Chua co du lieu thong ke.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="entry" items="${classCounts}">
                        <tr>
                            <td>${entry.key}</td>
                            <td>${entry.value}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </table>
    </div>
</div>
</body>
</html>
