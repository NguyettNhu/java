<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Biểu mẫu ${moduleTitle}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/static/style.css">
    </head>
    <body>
        <main class="shell">
            <section class="panel">
                <h1>Biểu mẫu ${moduleTitle}</h1>
                <p style="color:var(--danger)">${error}</p>
                <form method="post">
                    <input type="hidden" name="action" value="save">
                    <label>Mã</label>
                    <input name="ma" value="<c:out value='${item.ma}'/>" ${not empty item.ma?'readonly':''} required>
                    <label>Tên</label>
                    <input name="ten" value="<c:out value='${item.ten}'/>" required>
                    <c:choose>
                        <c:when test="${module=='sinh-vien'}">
                            <label>Lớp</label>
                            <input name="lop" value="<c:out value='${item.lop}'/>" required>
                        </c:when>
                        <c:when test="${module=='sach'}">
                            <label>Tác giả</label>
                            <input name="tacGia" value="<c:out value='${item.tacGia}'/>" required>
                        </c:when>
                        <c:otherwise>
                            <label>Giá</label>
                            <input type="number" min="1" step="0.01" name="gia" value="<c:out value='${item.gia}'/>" required>
                        </c:otherwise>
                    </c:choose>
                    <div class="actions">
                        <button>Lưu</button>
                        <a href="${pageContext.request.contextPath}/admin/${module}">Hủy</a>
                    </div>
                </form>
            </section>
        </main>
    </body>
</html>
