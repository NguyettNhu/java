package com.yukihira.bookstore.publisher;

import com.yukihira.bookstore.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "publishers", uniqueConstraints =
        @UniqueConstraint(name = "uk_publishers_name", columnNames = "name"))
public class Publisher extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 255)
    private String address;

    protected Publisher() {
    }

    public Publisher(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public String getAddress() { return address; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
}
