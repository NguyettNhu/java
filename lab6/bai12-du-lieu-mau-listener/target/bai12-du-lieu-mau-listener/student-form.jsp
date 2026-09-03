<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="isEdit" value="${formMode eq 'update'}" />
<!DOCTYPE html>
<html>
<head>
    <title><c:choose><c:when test="${isEdit}">Cap nhat sinh vien</c:when><c:otherwise>Them sinh vien</c:otherwise></c:choose></title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        form { max-width: 540px; }
        input { width: 100%; padding: 10px; margin: 8px 0 16px; box-sizing: border-box; }
        button, a.button { display: inline-block; padding: 10px 16px; border-radius: 8px; text-decoration: none; }
        button { border: 0; background: #0f5fa8; color: #fff; cursor: pointer; }
        a.button { background: #eef3f8; color: #234; margin-left: 8px; }
        .error { color: #c62828; }
    </style>
</head>
<body>
<h2>
    <c:choose>
        <c:when test="${isEdit}">Cap nhat thong tin sinh vien</c:when>
        <c:otherwise>Them sinh vien</c:otherwise>
    </c:choose>
</h2>

<p class="error">${error}</p>

<form action="${pageContext.request.contextPath}/students" method="post">
    <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">

    <label>Ma sinh vien:</label>
    <input type="text" name="id" value="${student.id}" ${isEdit ? 'readonly' : ''}>

    <label>Ho ten:</label>
    <input type="text" name="name" value="${student.name}">

    <label>Lop:</label>
    <input type="text" name="className" value="${student.className}">

    <label>Email:</label>
    <input type="email" name="email" value="${student.email}">

    <button type="submit">
        <c:choose>
            <c:when test="${isEdit}">Luu cap nhat</c:when>
            <c:otherwise>Luu sinh vien</c:otherwise>
        </c:choose>
    </button>
    <a class="button" href="${pageContext.request.contextPath}/students">Quay lai danh sach</a>
</form>
</body>
</html>
