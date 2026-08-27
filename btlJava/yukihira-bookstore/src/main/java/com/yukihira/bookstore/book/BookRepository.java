package com.yukihira.bookstore.book;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Optional<Book> findBySlug(String slug);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select book from Book book where book.id = :id")
    Optional<Book> findForUpdate(@Param("id") Long id);

    boolean existsBySlug(String slug);
    boolean existsByIsbn(String isbn);
    List<Book> findTop8ByStatusOrderByCreatedAtDesc(BookStatus status);
    long countByStatus(BookStatus status);
    long countByCategoryId(Long categoryId);
    long countByPublisherId(Long publisherId);
    long countByAuthorsId(Long authorId);
}
