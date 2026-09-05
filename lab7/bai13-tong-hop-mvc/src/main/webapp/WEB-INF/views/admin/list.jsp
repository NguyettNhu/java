<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>${moduleTitle}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
    </head>
    <body>
        <main class="shell">
            <div class="top">
                <div class="brand">Quản lý ${moduleTitle}</div>
                <a href="${pageContext.request.contextPath}/admin/index.jsp">Menu quản trị</a>
            </div>
            <p>
                <a class="button" href="?action=new">+ Thêm mới</a>
            </p>
            <table>
                <tr>
                    <th>Mã</th>
                    <th>Tên</th>
                    <c:choose>
                        <c:when test="${module == 'sinh-vien'}">
                            <th>Lớp</th>
                        </c:when>
                        <c:when test="${module == 'sach'}">
                            <th>Tác giả</th>
                        </c:when>
                        <c:otherwise>
                            <th>Giá</th>
                        </c:otherwise>
                    </c:choose>
                    <th>Thao tác</th>
                </tr>
                <c:forEach var="item" items="${items}">
                    <tr>
                        <td>${item.ma}</td>
                        <td>${item.ten}</td>
                        <c:choose>
                            <c:when test="${module == 'sinh-vien'}">
                                <td>${item.lop}</td>
                            </c:when>
                            <c:when test="${module == 'sach'}">
                                <td>${item.tacGia}</td>
                            </c:when>
                            <c:otherwise>
                                <td>${item.gia}</td>
                            </c:otherwise>
                        </c:choose>
                        <td>
                            <div class="actions">
                                <a class="button" href="?action=edit&ma=${item.ma}">Sửa</a>
                                <form method="post">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="ma" value="${item.ma}">
                                    <button class="danger">Xóa</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </main>
    </body>
</html>
