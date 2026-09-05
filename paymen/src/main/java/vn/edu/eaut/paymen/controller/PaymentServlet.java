package vn.edu.eaut.paymen.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;
import vn.edu.eaut.paymen.model.Payment;

@WebServlet("/thanh-toan")
public class PaymentServlet extends HttpServlet {
    private static final Set<String> PAYMENT_METHODS =
            Set.of("cash", "bank-transfer", "card");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("payment", new Payment());
        showForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Payment payment = readPayment(request);
        String validationError = validate(payment);

        if (validationError != null) {
            request.setAttribute("error", validationError);
            request.setAttribute("payment", payment);
            showForm(request, response);
            return;
        }

        request.setAttribute("payment", payment);
        request.setAttribute("transactionCode", createTransactionCode());
        request.setAttribute("paymentTime", formatPaymentTime());
        request.setAttribute("formattedAmount", formatAmount(payment.getAmount()));
        request.setAttribute(
                "paymentMethodLabel", formatPaymentMethod(payment.getPaymentMethod()));
        request.getRequestDispatcher("/WEB-INF/views/payment-result.jsp")
                .forward(request, response);
    }

    private Payment readPayment(HttpServletRequest request) {
        Payment payment = new Payment();
        payment.setOrderCode(value(request.getParameter("orderCode")));
        payment.setCustomerName(value(request.getParameter("customerName")));
        payment.setEmail(value(request.getParameter("email")));
        payment.setPhone(value(request.getParameter("phone")));
        payment.setPaymentMethod(value(request.getParameter("paymentMethod")));
        payment.setNote(value(request.getParameter("note")));

        payment.setAmount(parseAmount(request.getParameter("amount")));

        return payment;
    }

    private String validate(Payment payment) {
        if (payment.getOrderCode().isBlank()) {
            return "Mã đơn hàng không được để trống.";
        }
        if (payment.getCustomerName().isBlank()) {
            return "Họ tên khách hàng không được để trống.";
        }
        if (payment.getEmail().isBlank() || !payment.getEmail().contains("@")) {
            return "Email không hợp lệ.";
        }
        if (!payment.getPhone().matches("[0-9]{9,11}")) {
            return "Số điện thoại phải gồm từ 9 đến 11 chữ số.";
        }
        if (payment.getAmount() == null || payment.getAmount().signum() <= 0) {
            return "Số tiền thanh toán phải lớn hơn 0.";
        }
        if (!PAYMENT_METHODS.contains(payment.getPaymentMethod())) {
            return "Vui lòng chọn phương thức thanh toán.";
        }
        return null;
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/payment-form.jsp")
                .forward(request, response);
    }

    private String createTransactionCode() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String formatPaymentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    private BigDecimal parseAmount(String input) {
        String digits = value(input).replace(" ", "");
        if (!digits.matches("[0-9]+")) {
            return null;
        }

        try {
            return new BigDecimal(digits);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String formatAmount(BigDecimal amount) {
        String digits = amount.toBigIntegerExact().toString();
        return digits.replaceAll("\\B(?=(\\d{3})+(?!\\d))", " ");
    }

    private String formatPaymentMethod(String paymentMethod) {
        return switch (paymentMethod) {
            case "cash" -> "Tiền mặt";
            case "bank-transfer" -> "Chuyển khoản";
            case "card" -> "Thẻ";
            default -> paymentMethod;
        };
    }

    private String value(String input) {
        return input == null ? "" : input.trim();
    }
}
