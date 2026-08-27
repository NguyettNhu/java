package com.yukihira.bookstore.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutForm {

    @NotBlank(message = "Vui lòng nhập tên người nhận")
    @Size(max = 100, message = "Tên người nhận tối đa 100 ký tự")
    private String receiverName;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(
            regexp = "^(?:\\+84|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-46-9])\\d{7}$",
            message = "Số điện thoại phải là số Việt Nam hợp lệ, ví dụ 0912345678"
    )
    private String receiverPhone;

    @NotBlank(message = "Vui lòng nhập địa chỉ giao hàng")
    @Size(min = 10, max = 255, message = "Địa chỉ phải từ 10 đến 255 ký tự")
    @Pattern(
            regexp = "^(?=.*\\p{L})[\\p{L}\\p{N}][\\p{L}\\p{N}\\s,./-]*[\\p{L}\\p{N}]$",
            message = "Địa chỉ chỉ được chứa chữ, số và các dấu , . / -"
    )
    private String shippingAddress;

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String note;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    public String getReceiverName() { return receiverName; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getShippingAddress() { return shippingAddress; }
    public String getNote() { return note; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setNote(String note) { this.note = note; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}
