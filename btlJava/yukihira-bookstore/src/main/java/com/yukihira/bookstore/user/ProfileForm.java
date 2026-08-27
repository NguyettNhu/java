package com.yukihira.bookstore.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProfileForm {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Pattern(regexp = "^$|^[0-9+() .-]{8,20}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
}
