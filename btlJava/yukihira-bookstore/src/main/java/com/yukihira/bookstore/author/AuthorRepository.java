package com.yukihira.bookstore.author;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Author> {
    List<Author> findAllByOrderByNameAsc();
    Optional<Author> findByNameIgnoreCase(String name);
}
