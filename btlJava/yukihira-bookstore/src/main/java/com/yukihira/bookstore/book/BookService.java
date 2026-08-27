package com.yukihira.bookstore.book;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.common.util.Slugifier;
import com.yukihira.bookstore.publisher.PublisherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,
                       AuthorRepository authorRepository, PublisherRepository publisherRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public Page<BookView> search(BookSearchQuery filter, int page, int size, boolean activeOnly) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 48), sort(filter.sort()));
        return bookRepository.findAll(BookSpecifications.from(filter, activeOnly), pageable).map(this::toView);
    }

    @Transactional(readOnly = true)
    public List<BookView> featured() {
        return bookRepository.findTop8ByStatusOrderByCreatedAtDesc(BookStatus.ACTIVE).stream()
                .map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public BookView findActiveBySlug(String slug) {
        Book book = bookRepository.findBySlug(slug).orElseThrow();
        if (book.getStatus() != BookStatus.ACTIVE) throw new java.util.NoSuchElementException();
        return toView(book);
    }

    @Transactional(readOnly = true)
    public BookForm getForm(Long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        BookForm form = new BookForm();
        form.setId(book.getId());
        form.setTitle(book.getTitle());
        form.setIsbn(book.getIsbn());
        form.setDescription(book.getDescription());
        form.setPrice(book.getPrice());
        form.setStock(book.getStock());
        form.setImageUrl(book.getImageUrl());
        form.setCategoryId(book.getCategory().getId());
        form.setPublisherId(book.getPublisher() == null ? null : book.getPublisher().getId());
        form.setAuthorIds(book.getAuthors().stream().map(Author::getId).collect(java.util.stream.Collectors.toSet()));
        form.setStatus(book.getStatus());
        return form;
    }

    @Transactional
    public Book save(BookForm form) {
        String isbn = blankToNull(form.getIsbn());
        bookRepository.findAll().stream()
                .filter(book -> isbn != null && isbn.equalsIgnoreCase(book.getIsbn()))
                .filter(book -> !book.getId().equals(form.getId()))
                .findFirst()
                .ifPresent(book -> { throw new IllegalArgumentException("ISBN đã tồn tại"); });

        Category category = categoryRepository.findById(form.getCategoryId()).orElseThrow();
        Book book = form.getId() == null
                ? new Book(form.getTitle().trim(), uniqueSlug(form.getTitle()), form.getPrice(), form.getStock(), category)
                : bookRepository.findById(form.getId()).orElseThrow();
        book.setTitle(form.getTitle().trim());
        book.setIsbn(isbn);
        book.setDescription(blankToNull(form.getDescription()));
        book.setPrice(form.getPrice());
        book.setStock(form.getStock());
        book.setImageUrl(blankToNull(form.getImageUrl()));
        book.setCategory(category);
        book.setPublisher(form.getPublisherId() == null ? null
                : publisherRepository.findById(form.getPublisherId()).orElseThrow());
        Set<Author> authors = new LinkedHashSet<>(authorRepository.findAllById(form.getAuthorIds()));
        book.setAuthors(authors);
        book.setStatus(form.getStock() == 0 && form.getStatus() == BookStatus.ACTIVE
                ? BookStatus.OUT_OF_STOCK : form.getStatus());
        return bookRepository.save(book);
    }

    @Transactional
    public void deactivate(Long id) {
        Book book = bookRepository.findById(id).orElseThrow();
        book.setStatus(BookStatus.INACTIVE);
    }

    @Transactional(readOnly = true)
    public List<Category> categories() {
        return categoryRepository.findAllByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Author> authors() {
        return authorRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<com.yukihira.bookstore.publisher.Publisher> publishers() {
        return publisherRepository.findAllByOrderByNameAsc();
    }

    private BookView toView(Book book) {
        return new BookView(book.getId(), book.getTitle(), book.getSlug(), book.getIsbn(), book.getDescription(),
                book.getPrice(), book.getStock(), book.getImageUrl(), book.getStatus(), book.getCategory().getId(),
                book.getCategory().getName(), book.getPublisher() == null ? null : book.getPublisher().getName(),
                book.getAuthors().stream().map(Author::getName).sorted().toList());
    }

    private Sort sort(String value) {
        return switch (value == null ? "newest" : value.toLowerCase(Locale.ROOT)) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "title" -> Sort.by("title").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private String uniqueSlug(String title) {
        String base = Slugifier.toSlug(title);
        String slug = base;
        int suffix = 2;
        while (bookRepository.existsBySlug(slug)) slug = base + "-" + suffix++;
        return slug;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
