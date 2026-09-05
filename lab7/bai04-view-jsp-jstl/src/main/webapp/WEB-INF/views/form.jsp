<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Biểu mẫu</title>
    </head>
    <body>
        <c:set var="editing" value="${not empty sinhVien.ma}"/>
        <h1>${editing?'Cập nhật':'Thêm'} sinh viên</h1>
        <form method="post">
            <input type="hidden" name="action" value="save">
            <p>Mã sinh viên <input name="ma" value="<c:out value='${sinhVien.ma}'/>" ${editing?'readonly':''} required>
            </p>
            <p>Họ tên <input name="hoTen" value="<c:out value='${sinhVien.hoTen}'/>" required>
            </p>
            <p>Lớp <input name="lop" value="<c:out value='${sinhVien.lop}'/>" required>
            </p>
            <p>Email <input type="email" name="email" value="<c:out value='${sinhVien.email}'/>" required>
            </p>
            <button>Lưu</button> <a href="${pageContext.request.contextPath}/sinh-vien">Hủy</a>
            </form>
        </body>
    </html>
