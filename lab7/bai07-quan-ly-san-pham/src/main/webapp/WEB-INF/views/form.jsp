<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Biểu mẫu sản phẩm</h1>
<p style="color:red">${error}</p>
<form method="post">
    <p>Mã <input name="ma" value="${sanPham.ma}" ${not empty sanPham.ma?'readonly':''} required>
    </p>
    <p>Tên <input name="ten" value="${sanPham.ten}" required>
    </p>
    <p>Mô tả <textarea name="moTa">${sanPham.moTa}</textarea>
    </p>
    <p>Giá <input type="number" min="1" step="0.01" name="gia" value="${sanPham.gia}" required>
    </p>
    <p>Số lượng <input type="number" min="0" name="soLuong" value="${sanPham.soLuong}" required>
    </p>
    <button>Lưu</button>
</form>
