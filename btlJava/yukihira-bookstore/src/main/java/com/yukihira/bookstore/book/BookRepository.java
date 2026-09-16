package com.yukihira.bookstore.book;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    // Mọi trang hiển thị sách đều đọc thể loại và nhà xuất bản. Không nạp sẵn thì mỗi cuốn
    // sinh thêm truy vấn riêng, mà cơ sở dữ liệu đặt ở xa nên mỗi truy vấn tốn trọn một
    // lượt đi–về mạng. Chỉ nạp sẵn hai liên kết một–một để phân trang vẫn chạy trong SQL.
    @EntityGraph(attributePaths = {"category", "publisher"})
    Optional<Book> findBySlug(String slug);

    @Override
    @EntityGraph(attributePaths = {"category", "publisher"})
    Optional<Book> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"category", "publisher"})
    Page<Book> findAll(Specification<Book> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "publisher"})
    List<Book> findTop8ByStatusOrderByCreatedAtDesc(BookStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select book from Book book where book.id = :id")
    Optional<Book> findForUpdate(@Param("id") Long id);

    boolean existsBySlug(String slug);
    boolean existsByIsbn(String isbn);
    Optional<Book> findByIsbnIgnoreCase(String isbn);
    long countByStatus(BookStatus status);
    long countByCategoryId(Long categoryId);
    long countByPublisherId(Long publisherId);
    long countByAuthorsId(Long authorId);
}
