<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Biểu mẫu sách</h1>
<p style="color:red">${error}</p>
<form method="post">
    <input type="hidden" name="action" value="save">
    <p>Mã <input name="ma" value="${sach.ma}" ${not empty sach.ma?'readonly':''} required>
    </p>
    <p>Tên <input name="ten" value="${sach.ten}" required>
    </p>
    <p>Tác giả <input name="tacGia" value="${sach.tacGia}" required>
    </p>
    <p>Nhà xuất bản <input name="nhaXuatBan" value="${sach.nhaXuatBan}" required>
    </p>
    <p>Năm xuất bản <input type="number" name="namXuatBan" value="${sach.namXuatBan}" required>
    </p>
    <button>Lưu</button>
</form>
