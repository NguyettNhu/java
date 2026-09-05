<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Nhập điểm</h1>
<p style="color:red">${error}</p>
<form method="post">
    <p>Mã sinh viên <input name="maSinhVien" value="${diem.maSinhVien}" ${not empty diem.maSinhVien?'readonly':''} required>
    </p>
    <p>Họ tên <input name="hoTen" value="${diem.hoTen}" required>
    </p>
    <p>Chuyên cần <input type="number" min="0" max="10" step="0.1" name="chuyenCan" value="${diem.chuyenCan}" required>
    </p>
    <p>Giữa kỳ <input type="number" min="0" max="10" step="0.1" name="giuaKy" value="${diem.giuaKy}" required>
    </p>
    <p>Cuối kỳ <input type="number" min="0" max="10" step="0.1" name="cuoiKy" value="${diem.cuoiKy}" required>
    </p>
    <button>Lưu điểm</button>
</form>
