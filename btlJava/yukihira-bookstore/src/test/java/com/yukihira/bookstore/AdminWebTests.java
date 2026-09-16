package com.yukihira.bookstore;

import com.yukihira.bookstore.book.*;
import com.yukihira.bookstore.admin.catalog.ReferenceDataService;
import com.yukihira.bookstore.admin.catalog.ReferenceType;
import com.yukihira.bookstore.category.*;
import com.yukihira.bookstore.order.*;
import com.yukihira.bookstore.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdminWebTests {
    @Autowired WebApplicationContext context;
    @Autowired CategoryRepository categories;
    @Autowired BookService books;
    @Autowired BookRepository bookRepository;
    @Autowired UserRepository users;
    @Autowired UserService userService;
    @Autowired OrderRepository orders;
    @Autowired ReferenceDataService references;
    @Autowired PasswordEncoder passwords;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    void allAdminListsAndCreationFormsRender() throws Exception {
        mvc.perform(get("/images/yukihira.svg")).andExpect(status().isOk());
        for (String path : new String[]{"/admin", "/admin/books", "/admin/books/new", "/admin/categories",
                "/admin/categories/new", "/admin/authors", "/admin/authors/new", "/admin/publishers",
                "/admin/publishers/new", "/admin/inventory", "/admin/orders", "/admin/users", "/admin/reports"}) {
            mvc.perform(get(path).with(user("admin").roles("ADMIN"))).andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("admin-navigation")))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("/images/yukihira.svg")));
        }
    }

    @Test
    void detailAndEditPagesRenderDataAndInvalidIdsReturn404() throws Exception {
        var book = fixture();
        var customer = users.save(new User("Khách", UUID.randomUUID() + "@example.test", "encoded"));
        var order = new CustomerOrder("TEST-" + UUID.randomUUID().toString().substring(0, 8), customer, "An", "0901234567", "Hà Nội");
        order.addItem(new OrderItem(order, book, 1));
        orders.saveAndFlush(order);
        for (String path : new String[]{"/admin/books/" + book.getId(), "/admin/books/" + book.getId() + "/edit",
                "/admin/categories/" + book.getCategory().getId() + "/edit",
                "/admin/orders/" + order.getId(), "/admin/users/" + customer.getId()}) {
            mvc.perform(get(path).with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
        }
        mvc.perform(get("/admin/books/999999999").with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
        mvc.perform(get("/admin/orders/999999999").with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
    }

    @Test
    void customerCannotReadOrMutateAdminAndCsrfIsRequired() throws Exception {
        var book = fixture();
        for (String path : new String[]{"/admin/books", "/admin/orders", "/admin/users", "/admin/reports", "/admin/inventory"}) {
            mvc.perform(get(path).with(user("customer").roles("CUSTOMER"))).andExpect(status().isForbidden());
        }
        for (String path : new String[]{"/admin/books/" + book.getId() + "/delete",
                "/admin/books/" + book.getId() + "/deactivate", "/admin/books/save",
                "/admin/categories/save", "/admin/inventory/" + book.getId(),
                "/admin/users/1/status", "/admin/orders/1/status"}) {
            mvc.perform(post(path).with(user("customer").roles("CUSTOMER")).with(csrf())).andExpect(status().isForbidden());
            mvc.perform(post(path).with(user("admin").roles("ADMIN"))).andExpect(status().isForbidden());
        }
        assertThat(bookRepository.existsById(book.getId())).isTrue();
    }

    @Test
    void invalidFormShowsFieldErrorsAndDuplicateIsbnIsNotSaved() throws Exception {
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", " ").param("price", "-1").param("stock", "-1"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "title", "price", "stock", "categoryProvided"));
        var book = fixture();
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", "Sách trùng").param("price", "10000").param("stock", "1")
                .param("categoryId", book.getCategory().getId().toString()).param("isbn", book.getIsbn()))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "isbn"));
    }

    @Test
    void bookFormAcceptsTypedReferencesAndReusesThemOnEdit() throws Exception {
        String suffix = UUID.randomUUID().toString();
        String title = "Sách nhập tên " + suffix;
        String category = "Thể loại " + suffix;
        String author = "Tác giả " + suffix;
        mvc.perform(get("/admin/books/new").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("list=\"category-suggestions\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"authorNames\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"isbn-help\"")));
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", title).param("price", "120000").param("stock", "5")
                .param("categoryName", "  " + category + "  ")
                .param("authorNames", author + "\n" + author.toUpperCase(java.util.Locale.ROOT)))
                .andExpect(redirectedUrl("/admin/books"));
        var saved = books.searchAdmin(new BookSearchQuery(title, null, null, null, null, "title"), null, "", 0, 20)
                .getContent().getFirst();
        assertThat(saved.categoryName()).isEqualTo(category);
        assertThat(saved.authors()).containsExactly(author);
        var edit = books.getForm(saved.id());
        assertThat(edit.getCategoryName()).isEqualTo(category);
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("id", saved.id().toString()).param("version", edit.getVersion().toString())
                .param("title", title).param("price", "130000").param("stock", "5")
                .param("categoryName", category.toUpperCase(java.util.Locale.ROOT))
                .param("authorIds", edit.getAuthorIds().iterator().next().toString())
                .param("authorNames", author + "\nNgười viết thêm " + suffix))
                .andExpect(redirectedUrl("/admin/books"));
        var updated = books.adminBook(saved.id());
        assertThat(updated.categoryId()).isEqualTo(saved.categoryId());
        assertThat(updated.authors()).containsExactlyInAnyOrder(author, "Người viết thêm " + suffix);
        mvc.perform(get("/books/" + saved.slug())).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(category)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString(author)));
    }

    @Test
    void invalidTypedReferencesKeepInputAndDoNotCreateCategory() throws Exception {
        String category = "Chưa được lưu " + UUID.randomUUID();
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", "Sách lỗi").param("price", "120000").param("stock", "5")
                .param("categoryName", category).param("authorNames", "a".repeat(151)))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "authorNames"))
                .andExpect(model().attribute("form", org.hamcrest.Matchers.hasProperty("categoryName", org.hamcrest.Matchers.is(category))));
        assertThat(categories.existsByNameIgnoreCase(category)).isFalse();
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", "Sách lỗi").param("price", "120000").param("stock", "5")
                .param("categoryName", "a".repeat(101)))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "categoryName"));
    }

    @Test
    void catalogCrudFormsAndInventoryWorkEndToEnd() throws Exception {
        var book = fixture();
        mvc.perform(post("/admin/inventory/" + book.getId()).with(user("admin").roles("ADMIN")).with(csrf())
                .param("stock", "2").param("version", Long.toString(book.getVersion())))
                .andExpect(redirectedUrl("/admin/inventory")).andExpect(flash().attributeExists("success"));
        assertThat(books.adminBook(book.getId()).stock()).isEqualTo(2);
        mvc.perform(post("/admin/books/" + book.getId() + "/delete").with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl("/admin/books"));
        assertThat(bookRepository.existsById(book.getId())).isFalse();
    }

    @Test
    void analyticsFiltersRenderResolvedPeriodsAndRecoverFromInvalidInput() throws Exception {
        for (String period : new String[]{"day", "week", "month", "quarter", "year"}) {
            // Trang chỉ dựng bộ lọc và trỏ tới endpoint số liệu; việc tổng hợp diễn ra sau khi trang đã hiện.
            mvc.perform(get("/admin/reports").param("period", period).param("date", "2024-02-29")
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk()).andExpect(model().attributeDoesNotExist("analytics"))
                    .andExpect(model().attributeExists("analyticsUrl"))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("data-analytics-panel")));
            mvc.perform(get("/admin/analytics").param("for", "/admin/reports")
                            .param("period", period).param("date", "2024-02-29")
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk()).andExpect(model().attributeExists("analytics"))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("data-chart-type=\"pie\"")))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Top 5 khách chi tiêu nhiều nhất")));
        }
        // Trang thể loại vẫn dựng sẵn số liệu vì bảng quản lý của nó lấy dữ liệu từ đó.
        mvc.perform(get("/admin/categories").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(model().attributeExists("analytics"))
                .andExpect(model().attributeDoesNotExist("analyticsUrl"));
        // Đường dẫn không phải trang admin có báo cáo thì endpoint số liệu phải từ chối.
        mvc.perform(get("/admin/analytics").param("for", "/admin/../etc").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
        for (String query : new String[]{"period=custom&from=2024-02-29&to=2024-02-01", "period=custom", "date=not-a-date", "period=bad", "groupBy=bad"}) {
            mvc.perform(get("/admin/reports?" + query).with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk()).andExpect(model().attributeExists("analyticsError"))
                    .andExpect(model().attributeDoesNotExist("analytics"));
        }
        var book = fixture();
        mvc.perform(get("/admin/books").param("minPrice", "150000").param("maxPrice", "100000")
                        .with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andExpect(model().attributeExists("error"));
        mvc.perform(get("/admin/books").param("sort", "stock-desc").param("minPrice", "100000").param("maxPrice", "150000")
                        .with(user("admin").roles("ADMIN"))).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(book.getTitle())));
        mvc.perform(get("/admin/publishers").param("details", "Hà Nội").param("sort", "revenue")
                        .param("activity", "selling").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(model().attributeExists("items"));
    }

    @Test
    void invalidDateRangeShowsActionableError() throws Exception {
        mvc.perform(get("/admin/orders?from=2026-09-10&to=2026-09-01").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(model().attributeExists("error"));
        mvc.perform(get("/admin/reports?from=2026-09-10&to=2026-09-01").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk()).andExpect(model().attributeExists("error"));
    }

    @Test
    void adminLoginRedirectsToDashboardAndLockedCustomerCannotContinueSession() throws Exception {
        String email = UUID.randomUUID() + "@example.test";
        var admin = new User("Admin", email, passwords.encode("Test-password-123"));
        admin.setRole(Role.ADMIN);
        users.saveAndFlush(admin);
        mvc.perform(formLogin().user(email).password("Test-password-123")).andExpect(redirectedUrl("/admin"));
        var customer = users.saveAndFlush(new User("Khách", "customer-" + email, passwords.encode("Test-password-123")));
        userService.updateCustomerStatus(customer.getId(), UserStatus.LOCKED);
        mvc.perform(get("/cart").with(user(customer.getEmail()).roles("CUSTOMER")))
                .andExpect(redirectedUrl("/login?locked"));
        mvc.perform(formLogin().user(customer.getEmail()).password("Test-password-123"))
                .andExpect(redirectedUrl("/login?error"));
        userService.updateCustomerStatus(customer.getId(), UserStatus.ACTIVE);
        mvc.perform(formLogin().user(customer.getEmail()).password("Test-password-123")).andExpect(redirectedUrl("/"));
        assertThatThrownBy(() -> userService.updateCustomerStatus(admin.getId(), UserStatus.LOCKED))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bookPostCreatesUpdatesAndRejectsStaleFormsWithoutOverwritingStock() throws Exception {
        String token = UUID.randomUUID().toString();
        var category = categories.save(new Category("Kệ mới", "ke-" + token));
        String slug = "sach-web-" + token;
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("title", "  Sách từ form  ").param("slug", slug).param("price", "150000")
                .param("stock", "8").param("categoryId", category.getId().toString()).param("status", "ACTIVE"))
                .andExpect(redirectedUrl("/admin/books")).andExpect(flash().attributeExists("success"));
        var book = bookRepository.findBySlug(slug).orElseThrow();
        assertThat(book.getTitle()).isEqualTo("Sách từ form");
        String initialVersion = Long.toString(book.getVersion());
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("id", book.getId().toString()).param("version", initialVersion)
                .param("title", "Sách đã sửa").param("slug", slug).param("price", "160000")
                .param("stock", "6").param("categoryId", category.getId().toString()).param("status", "ACTIVE"))
                .andExpect(redirectedUrl("/admin/books"));
        assertThat(books.adminBook(book.getId()).title()).isEqualTo("Sách đã sửa");
        assertThat(books.adminBook(book.getId()).price()).isEqualByComparingTo("160000");
        mvc.perform(post("/admin/books/save").with(user("admin").roles("ADMIN")).with(csrf())
                .param("id", book.getId().toString()).param("version", initialVersion)
                .param("title", "Ghi đè từ tab cũ").param("price", "1").param("stock", "99")
                .param("categoryId", category.getId().toString()).param("status", "ACTIVE"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("form", "version"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Tải lại dữ liệu mới nhất")));
        assertThat(books.adminBook(book.getId()).stock()).isEqualTo(6);
        assertThat(books.adminBook(book.getId()).title()).isEqualTo("Sách đã sửa");
    }

    @Test
    void allReferenceTypesCanBeCreatedEditedFilteredAndDeletedThroughForms() throws Exception {
        for (ReferenceType type : ReferenceType.values()) {
            String name = "Mục " + UUID.randomUUID();
            String path = "/admin/" + type.getPath();
            mvc.perform(post(path + "/save").with(user("admin").roles("ADMIN")).with(csrf())
                    .param("name", name).param("details", "Nội dung ban đầu").param("active", "true"))
                    .andExpect(redirectedUrl(path)).andExpect(flash().attributeExists("success"));
            var item = references.search(type, name, 0).getContent().getFirst();
            mvc.perform(post(path + "/save").with(user("admin").roles("ADMIN")).with(csrf())
                    .param("id", item.id().toString()).param("name", name + " đã sửa")
                    .param("details", "Nội dung mới").param("active", "false"))
                    .andExpect(redirectedUrl(path));
            assertThat(references.getForm(type, item.id()).getDetails()).isEqualTo("Nội dung mới");
            mvc.perform(get(path).param("keyword", name).with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(name + " đã sửa")));
            mvc.perform(post(path + "/" + item.id() + "/delete").with(user("admin").roles("ADMIN")).with(csrf()))
                    .andExpect(redirectedUrl(path)).andExpect(flash().attributeExists("success"));
            assertThat(references.search(type, name, 0).getTotalElements()).isZero();
        }
    }

    @Test
    void orderStatusFormCompletesOrderAndRejectsChangingTerminalState() throws Exception {
        var book = fixture();
        var customer = users.save(new User("Khách", UUID.randomUUID() + "@example.test", "encoded"));
        var order = new CustomerOrder("WEB-" + UUID.randomUUID().toString().substring(0, 8), customer,
                "Nguyễn An", "0901234567", "123 Đường Sách, Hà Nội");
        order.addItem(new OrderItem(order, book, 2));
        orders.saveAndFlush(order);
        String path = "/admin/orders/" + order.getId();
        for (String target : new String[]{"CONFIRMED", "SHIPPING", "COMPLETED"}) {
            mvc.perform(post(path + "/status").param("status", target)
                    .with(user("admin").roles("ADMIN")).with(csrf()))
                    .andExpect(redirectedUrl(path)).andExpect(flash().attributeExists("success"));
            assertThat(orders.findById(order.getId()).orElseThrow().getStatus().name()).isEqualTo(target);
        }
        assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        mvc.perform(post(path + "/status").param("status", "CANCELLED")
                .with(user("admin").roles("ADMIN")).with(csrf()))
                .andExpect(redirectedUrl(path)).andExpect(flash().attributeExists("error"));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(book.getStock()).isEqualTo(5);
    }

    private Book fixture() {
        String token = UUID.randomUUID().toString();
        var category = categories.save(new Category("Kệ " + token, "ke-" + token));
        var form = new BookForm();
        form.setTitle("Sách " + token);
        form.setIsbn(token.substring(0, 20));
        form.setPrice(new BigDecimal("120000"));
        form.setStock(5);
        form.setCategoryId(category.getId());
        return books.save(form);
    }
}
