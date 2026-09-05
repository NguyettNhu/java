<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<h1>Khu vực quản trị</h1>
<p>Xin chào, ${sessionScope.username}</p>
<form action="${pageContext.request.contextPath}/logout" method="post">
    <button>Đăng xuất</button>
</form>
