package com.yukihira.bookstore.author;

import com.yukihira.bookstore.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "authors")
public class Author extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String biography;

    protected Author() {
    }

    public Author(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public String getBiography() { return biography; }
    public void setName(String name) { this.name = name; }
    public void setBiography(String biography) { this.biography = biography; }
}
