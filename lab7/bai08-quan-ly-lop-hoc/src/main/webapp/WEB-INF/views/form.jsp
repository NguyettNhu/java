<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Biểu mẫu lớp học</h1>
<p style="color:red">${error}</p>
<form method="post">
    <p>Mã lớp <input name="ma" value="${lopHoc.ma}" ${not empty lopHoc.ma?'readonly':''} required>
    </p>
    <p>Tên lớp <input name="ten" value="${lopHoc.ten}" required>
    </p>
    <p>Cố vấn học tập <input name="coVan" value="${lopHoc.coVan}" required>
    </p>
    <p>Số lượng sinh viên <input type="number" min="0" name="soLuongSinhVien" value="${lopHoc.soLuongSinhVien}" required>
    </p>
    <button>Lưu</button>
</form>
