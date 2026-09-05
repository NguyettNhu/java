<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>${empty sinhVien.ma ? 'Thêm' : 'Sửa'} sinh viên</h1>
<p style="color:red">${error}</p>
<form method="post">
    <input type="hidden" name="action" value="${empty sinhVien.ma ? 'create' : 'update'}">Mã: <input name="ma" value="${sinhVien.ma}" ${not empty sinhVien.ma?'readonly':''}>
    <br>Họ tên: <input name="hoTen" value="${sinhVien.hoTen}">
    <br>Lớp: <input name="lop" value="${sinhVien.lop}">
    <br>Email: <input name="email" value="${sinhVien.email}">
    <br>
    <button>Lưu</button> <a href="${pageContext.request.contextPath}/sinh-vien">Hủy</a>
    </form>
