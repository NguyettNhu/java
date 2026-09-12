package vn.edu.eaut.btthymeleaf.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class Course {

    private Long id;

    @NotBlank(message = "Vui lòng nhập tên khóa học")
    @Size(max = 100, message = "Tên khóa học không được quá 100 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng nhập tên giảng viên")
    @Size(max = 80, message = "Tên giảng viên không được quá 80 ký tự")
    private String instructor;

    @NotBlank(message = "Vui lòng chọn trình độ")
    private String level;

    @NotNull(message = "Vui lòng nhập học phí")
    @DecimalMin(value = "0", message = "Học phí không được là số âm")
    private BigDecimal price;

    @Size(max = 300, message = "Mô tả không được quá 300 ký tự")
    private String description;

    public Course() {
    }

    public Course(Long id, String name, String instructor, String level,
                  BigDecimal price, String description) {
        this.id = id;
        this.name = name;
        this.instructor = instructor;
        this.level = level;
        this.price = price;
        this.description = description;
    }

    public Course(Course other) {
        this(other.id, other.name, other.instructor, other.level, other.price, other.description);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
