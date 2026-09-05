<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thanh toán đơn hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/payment.css">
    <script defer src="${pageContext.request.contextPath}/static/payment.js"></script>
</head>
<body>
<main class="payment-layout">
    <section class="payment-intro">
        <span class="eyebrow">PAYMEN CHECKOUT</span>
        <h1>Thanh toán<br>đơn hàng</h1>
        <p>
            Điền thông tin bên cạnh để tạo giao dịch thanh toán mô phỏng.
            Hệ thống không lưu thông tin thẻ ngân hàng.
        </p>
        <div class="security-note">Bảo mật · Nhanh chóng · Minh bạch</div>
    </section>

    <section class="payment-card">
        <div class="card-heading">
            <div>
                <span class="step">Bước cuối</span>
                <h2>Thông tin thanh toán</h2>
            </div>
            <span class="lock">SSL</span>
        </div>

        <c:if test="${not empty error}">
            <div class="error-message">
                <c:out value="${error}"/>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/thanh-toan" method="post">
            <div class="form-grid">
                <label class="field">
                    <span>Mã đơn hàng</span>
                    <input
                            name="orderCode"
                            value="<c:out value='${payment.orderCode}'/>"
                            placeholder="VD: DH-2026-001"
                            required>
                </label>

                <label class="field">
                    <span>Số tiền (VNĐ)</span>
                    <input
                            id="amountDisplay"
                            type="text"
                            inputmode="numeric"
                            autocomplete="off"
                            value="<c:out value='${payment.amount}'/>"
                            pattern="[0-9 ]+"
                            maxlength="31"
                            placeholder="500 000"
                            aria-describedby="amountHint"
                            required>
                    <input
                            id="amount"
                            type="hidden"
                            name="amount"
                            value="<c:out value='${payment.amount}'/>">
                    <small id="amountHint" class="field-hint">
                        Số tiền tự động được tách nhóm, ví dụ: 1 500 000
                    </small>
                </label>

                <label class="field full-width">
                    <span>Họ và tên</span>
                    <input
                            name="customerName"
                            value="<c:out value='${payment.customerName}'/>"
                            placeholder="Nguyễn Văn An"
                            required>
                </label>

                <label class="field">
                    <span>Email</span>
                    <input
                            type="email"
                            name="email"
                            value="<c:out value='${payment.email}'/>"
                            placeholder="an@example.com"
                            required>
                </label>

                <label class="field">
                    <span>Số điện thoại</span>
                    <input
                            name="phone"
                            value="<c:out value='${payment.phone}'/>"
                            pattern="[0-9]{9,11}"
                            placeholder="0912345678"
                            required>
                </label>
            </div>

            <fieldset class="payment-methods">
                <legend>Phương thức thanh toán</legend>
                <label class="method-option">
                    <input
                            type="radio"
                            name="paymentMethod"
                            value="cash"
                            ${payment.paymentMethod == 'cash' ? 'checked' : ''}
                            required>
                    <span><strong>Tiền mặt</strong><small>Thanh toán khi nhận hàng</small></span>
                </label>
                <label class="method-option">
                    <input
                            type="radio"
                            name="paymentMethod"
                            value="bank-transfer"
                            ${payment.paymentMethod == 'bank-transfer' ? 'checked' : ''}>
                    <span><strong>Chuyển khoản</strong><small>Xác nhận qua ngân hàng</small></span>
                </label>
                <label class="method-option">
                    <input
                            type="radio"
                            name="paymentMethod"
                            value="card"
                            ${payment.paymentMethod == 'card' ? 'checked' : ''}>
                    <span><strong>Thẻ</strong><small>Giao dịch mô phỏng</small></span>
                </label>
            </fieldset>

            <label class="field">
                <span>Ghi chú</span>
                <textarea
                        name="note"
                        rows="3"
                        placeholder="Nội dung thanh toán"><c:out value="${payment.note}"/></textarea>
            </label>

            <button class="submit-button" type="submit">Xác nhận thanh toán</button>
        </form>
    </section>
</main>
</body>
</html>
