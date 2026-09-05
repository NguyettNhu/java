<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>Giỏ hàng Session</h1>
<table border="1" cellpadding="8">
    <tr>
        <th>Sản phẩm</th>
        <th>Giá</th>
        <th>Số lượng</th>
        <th>Thành tiền</th>
        <th>
        </th>
    </tr>
    <c:forEach var="i" items="${cart}">
        <tr>
            <td>${i.sanPham.ten}</td>
            <td>${i.sanPham.gia}</td>
            <td>
                <form method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="ma" value="${i.sanPham.ma}">
                    <input type="number" min="0" name="soLuong" value="${i.soLuong}">
                    <button>Cập nhật</button>
                </form>
            </td>
            <td>${i.thanhTien}</td>
            <td>
                <form method="post">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="ma" value="${i.sanPham.ma}">
                    <button>Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty cart}">
        <tr>
            <td colspan="5">Giỏ hàng trống</td>
        </tr>
    </c:if>
</table>
<h3>Tổng tiền: ${tongTien}</h3>
<a href="${pageContext.request.contextPath}/san-pham">Tiếp tục mua</a>
