package com.yukihira.bookstore;

import com.yukihira.bookstore.admin.catalog.*;
import com.yukihira.bookstore.author.*;
import com.yukihira.bookstore.book.*;
import com.yukihira.bookstore.cart.*;
import com.yukihira.bookstore.category.*;
import com.yukihira.bookstore.order.*;
import com.yukihira.bookstore.publisher.*;
import com.yukihira.bookstore.user.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminCatalogTests {
    @Autowired BookService books;
    @Autowired BookRepository bookRepository;
    @Autowired CategoryRepository categories;
    @Autowired AuthorRepository authors;
    @Autowired PublisherRepository publishers;
    @Autowired ReferenceDataService references;
    @Autowired OrderRepository orders;
    @Autowired UserRepository users;
    @Autowired CartRepository carts;
    @Autowired CartService cartService;

    @Test
    void bookCanBeCreatedReadUpdatedAndDeletedWhenUnused() {
        var book = books.save(form());
        var edit = books.getForm(book.getId());
        edit.setTitle("Tên sách mới");
        edit.setSlug("ten-sach-moi-" + book.getId());
        edit.setPrice(new BigDecimal("99000.50"));
        books.save(edit);
        var detail = books.adminBook(book.getId());
        assertThat(detail.title()).isEqualTo("Tên sách mới");
        assertThat(detail.price()).isEqualByComparingTo("99000.50");
        assertThat(books.delete(book.getId())).contains("Đã xóa");
        assertThat(bookRepository.existsById(book.getId())).isFalse();
    }

    @Test
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void failedBookSaveRollsBackNewCategory() {
        String name = "Thể loại rollback " + token();
        var form = new BookForm();
        form.setTitle("Sách chưa lưu " + token());
        form.setPrice(new BigDecimal("10000"));
        form.setStock(1);
        form.setCategoryName(name);
        form.setPublisherId(Long.MAX_VALUE);
        assertThatThrownBy(() -> books.save(form)).isInstanceOf(CatalogValidationException.class)
                .extracting("field").isEqualTo("publisherId");
        assertThat(categories.existsByNameIgnoreCase(name)).isFalse();
    }

    @Test
    void duplicateIsbnAndSlugAttachToCorrectFields() {
        var first = books.save(form());
        var second = form();
        second.setIsbn(first.getIsbn().toUpperCase());
        assertThatThrownBy(() -> books.save(second)).isInstanceOf(CatalogValidationException.class)
                .extracting("field").isEqualTo("isbn");
        second.setIsbn(null);
        second.setSlug(first.getSlug());
        assertThatThrownBy(() -> books.save(second)).isInstanceOf(CatalogValidationException.class)
                .extracting("field").isEqualTo("slug");
    }

    @Test
    void invalidMoneyStockStatusAndImageAreRejectedBeforeSaving() {
        var form = form();
        form.setPrice(new BigDecimal("-1"));
        form.setStock(-2);
        form.setStatus(null);
        form.setImageUrl("javascript:alert(1)");
        assertThatThrownBy(() -> books.save(form)).isInstanceOf(ConstraintViolationException.class);
        form.setPrice(new BigDecimal("10000000000000"));
        assertThatThrownBy(() -> books.save(form)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void missingAuthorCannotSilentlyDisappearFromBook() {
        var form = form();
        form.setAuthorIds(Set.of(Long.MAX_VALUE));
        assertThatThrownBy(() -> books.save(form)).isInstanceOf(CatalogValidationException.class)
                .extracting("field").isEqualTo("authorIds");
    }

    @Test
    void stockUpdateRejectsStaleEditAndRespectsInactiveStatus() {
        var book = books.save(form());
        var stale = books.getForm(book.getId());
        var stock = new StockForm();
        stock.setVersion(book.getVersion());
        stock.setStock(0);
        books.updateStock(book.getId(), stock);
        assertThat(books.adminBook(book.getId()).status()).isEqualTo(BookStatus.OUT_OF_STOCK);
        assertThatThrownBy(() -> books.save(stale)).isInstanceOf(CatalogValidationException.class)
                .extracting("field").isEqualTo("version");
        stock.setVersion(books.adminBook(book.getId()).version());
        stock.setStock(7);
        books.updateStock(book.getId(), stock);
        assertThat(books.adminBook(book.getId()).status()).isEqualTo(BookStatus.ACTIVE);
        books.deactivate(book.getId());
        bookRepository.flush();
        stock.setVersion(books.adminBook(book.getId()).version());
        stock.setStock(9);
        books.updateStock(book.getId(), stock);
        assertThat(books.adminBook(book.getId()).status()).isEqualTo(BookStatus.INACTIVE);
    }

    @Test
    void deletingSoldBookPreservesOrderSnapshot() {
        var book = books.save(form());
        var customer = users.save(new User("Khách", token() + "@example.test", "encoded"));
        var order = new CustomerOrder("TEST-" + token().substring(0, 8), customer, "An", "0901234567", "Hà Nội");
        order.addItem(new OrderItem(order, book, 2));
        orders.saveAndFlush(order);
        assertThat(books.delete(book.getId())).contains("giữ lịch sử");
        assertThat(books.adminBook(book.getId()).status()).isEqualTo(BookStatus.INACTIVE);
        assertThat(orders.findDetailedById(order.getId()).orElseThrow().getItems().getFirst().getUnitPrice())
                .isEqualByComparingTo("120000");
    }

    @Test
    void deletingBookInCartDeactivatesItAndKeepsCartReadable() {
        var book = books.save(form());
        var customer = users.save(new User("Khách", token() + "@example.test", "encoded"));
        carts.save(new Cart(customer));
        cartService.add(customer.getEmail(), book.getId(), 1);
        books.delete(book.getId());
        assertThat(cartService.getCart(customer.getEmail()).itemCount()).isEqualTo(1);
        assertThat(books.adminBook(book.getId()).status()).isEqualTo(BookStatus.INACTIVE);
    }

    @Test
    void searchWithMultipleAuthorsHasAccuratePaginationAndFilters() {
        var form = form();
        var first = authors.save(new Author("SearchAuthor " + token()));
        var second = authors.save(new Author("SearchAuthor " + token()));
        form.setAuthorIds(Set.of(first.getId(), second.getId()));
        form.setStock(3);
        books.save(form);
        var filter = new BookSearchQuery(form.getTitle(), form.getCategoryId(), null, null, null, "title");
        var found = books.searchAdmin(filter, BookStatus.ACTIVE, "low", 0, 1);
        assertThat(found.getTotalElements()).isEqualTo(1);
        assertThat(found.getTotalPages()).isEqualTo(1);
        assertThat(books.searchAdmin(filter, BookStatus.INACTIVE, "", 0, 20)).isEmpty();
    }

    @Test
    void referencesEnforceColumnLengthsAndProtectUsedEntries() {
        var category = new ReferenceForm();
        category.setName("a".repeat(101));
        assertThatThrownBy(() -> references.save(ReferenceType.CATEGORIES, category))
                .isInstanceOf(CatalogValidationException.class).extracting("field").isEqualTo("name");
        var publisher = new ReferenceForm();
        publisher.setName("Nhà xuất bản");
        publisher.setDetails("a".repeat(256));
        assertThatThrownBy(() -> references.save(ReferenceType.PUBLISHERS, publisher))
                .isInstanceOf(CatalogValidationException.class).extracting("field").isEqualTo("details");

        var form = form();
        var author = authors.save(new Author("Tác giả " + token()));
        var press = publishers.save(new Publisher("NXB " + token()));
        form.setAuthorIds(Set.of(author.getId()));
        form.setPublisherId(press.getId());
        books.save(form);
        assertThat(references.delete(ReferenceType.CATEGORIES, form.getCategoryId())).contains("tạm ẩn");
        assertThat(categories.findById(form.getCategoryId()).orElseThrow().isActive()).isFalse();
        assertThatThrownBy(() -> references.delete(ReferenceType.AUTHORS, author.getId()))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> references.delete(ReferenceType.PUBLISHERS, press.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void referencesSupportCreateEditSearchAndDelete() {
        for (var type : ReferenceType.values()) {
            var form = new ReferenceForm();
            form.setName("Danh mục " + token());
            references.save(type, form);
            var item = references.search(type, form.getName(), 0).getContent().getFirst();
            var edit = references.getForm(type, item.id());
            edit.setDetails("Nội dung đã sửa");
            references.save(type, edit);
            assertThat(references.getForm(type, item.id()).getDetails()).isEqualTo("Nội dung đã sửa");
            references.delete(type, item.id());
            assertThat(references.search(type, form.getName(), 0)).isEmpty();
        }
    }

    private BookForm form() {
        var token = token();
        var category = categories.save(new Category("Kệ " + token, "ke-" + token));
        var form = new BookForm();
        form.setTitle("Sách " + token);
        form.setIsbn(token.substring(0, 20));
        form.setPrice(new BigDecimal("120000"));
        form.setStock(10);
        form.setCategoryId(category.getId());
        return form;
    }
    private String token() { return UUID.randomUUID().toString(); }
}
