package com.yukihira.bookstore.book;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.admin.catalog.CatalogValidationException;
import com.yukihira.bookstore.cart.CartItemRepository;
import com.yukihira.bookstore.order.OrderItemRepository;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import java.util.Objects;
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
@Validated
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository,
                       AuthorRepository authorRepository, PublisherRepository publisherRepository,
                       OrderItemRepository orderItemRepository, CartItemRepository cartItemRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
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
    public Page<BookView> searchAdmin(BookSearchQuery filter, BookStatus status, String stock, int page, int size) {
        // Lọc sách admin theo trạng thái và mức tồn kho để tạo cảnh báo dashboard.
        var spec = BookSpecifications.from(filter, false);
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        if ("low".equals(stock)) spec = spec.and((root, query, cb) -> cb.and(
                cb.between(root.get("stock"), 1, 5), cb.notEqual(root.get("status"), BookStatus.INACTIVE)));
        if ("empty".equals(stock)) spec = spec.and((root, query, cb) -> cb.equal(root.get("stock"), 0));
        return bookRepository.findAll(spec, PageRequest.of(Math.max(0, page), Math.clamp(size, 1, 48),
                sort(filter.sort()).and(Sort.by("id")))).map(this::toView);
    }

    @Transactional(readOnly = true)
    public BookView adminBook(Long id) {
        return toView(bookRepository.findById(id).orElseThrow());
    }

    @Transactional(readOnly = true)
    public List<Category> adminCategories() {
        return categoryRepository.findAll(Sort.by("name"));
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
        form.setVersion(book.getVersion());
        form.setSlug(book.getSlug());
        form.setTitle(book.getTitle());
        form.setIsbn(book.getIsbn());
        form.setDescription(book.getDescription());
        form.setPrice(book.getPrice());
        form.setStock(book.getStock());
        form.setImageUrl(book.getImageUrl());
        form.setCategoryId(book.getCategory().getId());
        form.setCategoryName(book.getCategory().getName());
        form.setPublisherId(book.getPublisher() == null ? null : book.getPublisher().getId());
        form.setAuthorIds(book.getAuthors().stream().map(Author::getId).collect(java.util.stream.Collectors.toSet()));
        form.setStatus(book.getStatus());
        return form;
    }

    @Transactional
    public Book save(@Valid BookForm form) {
        String isbn = blankToNull(form.getIsbn());
        if (isbn != null) bookRepository.findByIsbnIgnoreCase(isbn)
                .filter(book -> !book.getId().equals(form.getId()))
                .ifPresent(book -> { throw new CatalogValidationException("isbn", "ISBN đã tồn tại"); });

        var enteredAuthors = form.getAuthorNames() == null ? List.<String>of()
                : form.getAuthorNames().lines().map(String::trim).filter(name -> !name.isEmpty()).toList();
        if (enteredAuthors.stream().anyMatch(name -> name.length() > 150)) {
            throw new CatalogValidationException("authorNames", "Mỗi tên tác giả không quá 150 ký tự");
        }
        Category category = resolveCategory(form);
        Book book = form.getId() == null
                ? new Book(form.getTitle().trim(), uniqueSlug(form.getTitle()), form.getPrice(), form.getStock(), category)
                : bookRepository.findForUpdate(form.getId()).orElseThrow();
        if (form.getId() != null) checkVersion(book, form.getVersion());
        String slug = blankToNull(form.getSlug());
        if (slug != null) {
            bookRepository.findBySlug(slug).filter(item -> !Objects.equals(item.getId(), form.getId()))
                    .ifPresent(item -> { throw new CatalogValidationException("slug", "Đường dẫn đã tồn tại"); });
            book.setSlug(slug);
        }
        book.setTitle(form.getTitle().trim());
        book.setIsbn(isbn);
        book.setDescription(blankToNull(form.getDescription()));
        book.setPrice(form.getPrice());
        book.setStock(form.getStock());
        book.setImageUrl(blankToNull(form.getImageUrl()));
        book.setCategory(category);
        book.setPublisher(form.getPublisherId() == null ? null
                : publisherRepository.findById(form.getPublisherId())
                    .orElseThrow(() -> new CatalogValidationException("publisherId", "Nhà xuất bản không còn tồn tại")));
        Set<Author> authors = new LinkedHashSet<>(authorRepository.findAllById(form.getAuthorIds()));
        if (authors.size() != form.getAuthorIds().size()) {
            throw new CatalogValidationException("authorIds", "Một tác giả đã bị xóa. Hãy chọn lại tác giả.");
        }
        for (String name : enteredAuthors) {
            authors.add(authorRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> authorRepository.save(new Author(name))));
        }
        book.setAuthors(authors);
        book.setStatus(normalizeStatus(form.getStatus(), form.getStock()));
        return bookRepository.saveAndFlush(book);
    }

    @Transactional
    public void deactivate(Long id) {
        Book book = bookRepository.findForUpdate(id).orElseThrow();
        book.setStatus(BookStatus.INACTIVE);
    }

    @Transactional
    public String delete(Long id) {
        Book book = bookRepository.findForUpdate(id).orElseThrow();
        if (orderItemRepository.existsByBookId(id) || cartItemRepository.existsByBookId(id)) {
            book.setStatus(BookStatus.INACTIVE);
            return "Sách đã có trong đơn hàng hoặc giỏ hàng nên được chuyển sang ngừng bán để giữ lịch sử.";
        }
        bookRepository.delete(book);
        bookRepository.flush();
        return "Đã xóa sách chưa phát sinh giao dịch.";
    }

    @Transactional
    public void updateStock(Long id, @Valid StockForm form) {
        Book book = bookRepository.findForUpdate(id).orElseThrow();
        checkVersion(book, form.getVersion());
        book.setStock(form.getStock());
        book.setStatus(normalizeStatus(book.getStatus(), form.getStock()));
        bookRepository.flush();
    }

    private void checkVersion(Book book, Long version) {
        if (version == null || version != book.getVersion()) {
            throw new CatalogValidationException("version",
                    "Sách hoặc tồn kho đã thay đổi. Hãy tải lại trang rồi nhập lại thay đổi của bạn.");
        }
    }

    private BookStatus normalizeStatus(BookStatus status, int stock) {
        return status == BookStatus.INACTIVE ? status : (stock == 0 ? BookStatus.OUT_OF_STOCK : BookStatus.ACTIVE);
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
                book.getAuthors().stream().map(Author::getName).sorted().toList(), book.getVersion());
    }

    private Sort sort(String value) {
        return switch (value == null ? "newest" : value.toLowerCase(Locale.ROOT)) {
            case "price-asc" -> Sort.by("price").ascending();
            case "price-desc" -> Sort.by("price").descending();
            case "stock-desc" -> Sort.by("stock").descending();
            case "stock-asc" -> Sort.by("stock").ascending();
            case "title" -> Sort.by("title").ascending();
            default -> Sort.by("createdAt").descending();
        };
    }

    private String uniqueSlug(String title) {
        String base = Slugifier.toSlug(title);
        if (base.isEmpty()) base = "sach";
        String slug = base;
        int suffix = 2;
        while (bookRepository.existsBySlug(slug)) slug = base + "-" + suffix++;
        return slug;
    }

    private Category resolveCategory(BookForm form) {
        String name = blankToNull(form.getCategoryName());
        if (name == null) {
            return categoryRepository.findById(form.getCategoryId())
                    .orElseThrow(() -> new CatalogValidationException("categoryId", "Thể loại không còn tồn tại"));
        }
        return categoryRepository.findByNameIgnoreCase(name).orElseGet(() -> {
            String base = Slugifier.toSlug(name);
            if (base.isEmpty()) base = "the-loai";
            String slug = base;
            int suffix = 2;
            while (categoryRepository.existsBySlug(slug)) slug = base + "-" + suffix++;
            return categoryRepository.save(new Category(name, slug));
        });
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
