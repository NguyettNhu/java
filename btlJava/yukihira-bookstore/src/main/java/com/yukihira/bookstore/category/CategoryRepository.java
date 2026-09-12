package com.yukihira.bookstore.category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Category> {
    Optional<Category> findBySlug(String slug);
    Optional<Category> findByNameIgnoreCase(String name);
    List<Category> findAllByActiveTrueOrderByNameAsc();
    boolean existsByNameIgnoreCase(String name);
    boolean existsBySlug(String slug);
}
