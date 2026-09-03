<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Danh sach sinh vien</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 32px; }
        .toolbar, .search-form { margin-bottom: 18px; }
        .toolbar a, .search-form button, .delete-button {
            display: inline-block;
            padding: 8px 14px;
            border: 0;
            border-radius: 8px;
            text-decoration: none;
            cursor: pointer;
        }
        .primary { background: #0f5fa8; color: #fff; }
        .secondary { background: #eef3f8; color: #234; }
        .danger { background: #c62828; color: #fff; }
        input[type="text"] { padding: 9px; min-width: 260px; }
        table { width: 100%; border-collapse: collapse; }
        th, td { border: 1px solid #d7dfe8; padding: 10px; text-align: left; }
        th { background: #f3f7fb; }
        .message { color: #0a7b34; }
        .empty { color: #c62828; }
        .inline-form { display: inline; }
    </style>
</head>
<body>
<h2>Danh sach sinh vien</h2>

<div class="toolbar">
    <a class="primary" href="${pageContext.request.contextPath}/students?action=form">Them sinh vien</a>
    <a class="secondary" href="${pageContext.request.contextPath}/welcome">Dashboard</a>
    <a class="secondary" href="${pageContext.request.contextPath}/logout">Dang xuat</a>
</div>

<form class="search-form" action="${pageContext.request.contextPath}/students" method="get">
    <input type="text" name="keyword" value="${keyword}" placeholder="Tim theo ho ten">
    <button class="primary" type="submit">Tim kiem</button>
    <a class="secondary" href="${pageContext.request.contextPath}/students">Lam moi</a>
</form>

<p class="message">${param.message}</p>
<p>Tong so sinh vien hien tai: <strong>${totalStudents}</strong></p>

<c:if test="${not empty emptyMessage}">
    <p class="empty">${emptyMessage}</p>
</c:if>

<table>
    <tr>
        <th>Ma SV</th>
        <th>Ho ten</th>
        <th>Lop</th>
        <th>Email</th>
        <th>Thao tac</th>
    </tr>

    <c:choose>
        <c:when test="${empty students}">
            <tr>
                <td colspan="5">Chua co du lieu hien thi.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="sv" items="${students}">
                <tr>
                    <td>${sv.id}</td>
                    <td>${sv.name}</td>
                    <td>${sv.className}</td>
                    <td>${sv.email}</td>
                    <td>
                        <a class="secondary" href="${pageContext.request.contextPath}/students?action=edit&id=${sv.id}">Sua</a>
                        <form class="inline-form" action="${pageContext.request.contextPath}/students" method="post">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${sv.id}">
                            <button class="delete-button danger" type="submit">Xoa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</table>
</body>
</html>
