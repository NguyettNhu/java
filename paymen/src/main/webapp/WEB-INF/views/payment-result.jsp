<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán thành công</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/payment.css">
</head>
<body>
<main class="result-layout">
    <section class="result-card">
        <div class="success-mark">✓</div>
        <span class="eyebrow">GIAO DỊCH THÀNH CÔNG</span>
        <h1>Cảm ơn bạn!</h1>
        <p>Yêu cầu thanh toán mô phỏng đã được tiếp nhận.</p>

        <dl class="receipt">
            <div>
                <dt>Mã giao dịch</dt>
                <dd><c:out value="${transactionCode}"/></dd>
            </div>
            <div>
                <dt>Mã đơn hàng</dt>
                <dd><c:out value="${payment.orderCode}"/></dd>
            </div>
            <div>
                <dt>Khách hàng</dt>
                <dd><c:out value="${payment.customerName}"/></dd>
            </div>
            <div>
                <dt>Số tiền</dt>
                <dd><c:out value="${formattedAmount}"/> VNĐ</dd>
            </div>
            <div>
                <dt>Phương thức</dt>
                <dd><c:out value="${paymentMethodLabel}"/></dd>
            </div>
            <div>
                <dt>Thời gian</dt>
                <dd><c:out value="${paymentTime}"/></dd>
            </div>
        </dl>

        <a class="back-button" href="${pageContext.request.contextPath}/thanh-toan">
            Tạo thanh toán mới
        </a>
    </section>
</main>
</body>
</html>
