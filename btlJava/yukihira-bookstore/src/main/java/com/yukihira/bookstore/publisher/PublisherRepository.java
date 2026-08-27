package com.yukihira.bookstore.publisher;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    List<Publisher> findAllByOrderByNameAsc();
    java.util.Optional<Publisher> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
