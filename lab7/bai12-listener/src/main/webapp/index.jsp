<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% session.setAttribute("visited", true); %>
<h1>Bài 12 - Listener</h1>
<p>Session hiện tại: <%= session.getId() %>
</p>
<p>Xem log trong console của Tomcat khi ứng dụng/session được tạo hoặc hủy.</p>
