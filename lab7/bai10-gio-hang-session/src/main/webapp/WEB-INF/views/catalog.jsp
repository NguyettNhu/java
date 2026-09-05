<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h1>Sản phẩm</h1>
<a href="${pageContext.request.contextPath}/gio-hang">Xem giỏ hàng</a>
<table border="1" cellpadding="8">
    <tr>
        <th>Mã</th>
        <th>Tên</th>
        <th>Giá</th>
        <th>
        </th>
    </tr>
    <c:forEach var="s" items="${danhSach}">
        <tr>
            <td>${s.ma}</td>
            <td>${s.ten}</td>
            <td>${s.gia}</td>
            <td>
                <form action="${pageContext.request.contextPath}/gio-hang" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="ma" value="${s.ma}">
                    <button>Thêm vào giỏ</button>
                </form>
            </td>
        </tr>
    </c:forEach>
</table>
