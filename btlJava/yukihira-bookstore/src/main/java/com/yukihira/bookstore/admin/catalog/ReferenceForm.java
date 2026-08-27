package com.yukihira.bookstore.admin.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReferenceForm {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên")
    @Size(max = 150, message = "Tên không quá 150 ký tự")
    private String name;

    @Size(max = 2000, message = "Nội dung không quá 2000 ký tự")
    private String details;

    private boolean active = true;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDetails() { return details; }
    public boolean isActive() { return active; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDetails(String details) { this.details = details; }
    public void setActive(boolean active) { this.active = active; }
}
