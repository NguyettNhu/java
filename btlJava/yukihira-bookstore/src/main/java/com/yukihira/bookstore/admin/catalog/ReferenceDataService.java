package com.yukihira.bookstore.admin.catalog;

import com.yukihira.bookstore.author.Author;
import com.yukihira.bookstore.author.AuthorRepository;
import com.yukihira.bookstore.book.BookRepository;
import com.yukihira.bookstore.category.Category;
import com.yukihira.bookstore.category.CategoryRepository;
import com.yukihira.bookstore.common.util.Slugifier;
import com.yukihira.bookstore.publisher.Publisher;
import com.yukihira.bookstore.publisher.PublisherRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReferenceDataService {

    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    public ReferenceDataService(CategoryRepository categoryRepository, AuthorRepository authorRepository,
                                PublisherRepository publisherRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public List<ReferenceView> list(ReferenceType type) {
        return switch (type) {
            case CATEGORIES -> categoryRepository.findAll(Sort.by("name")).stream()
                    .map(item -> new ReferenceView(item.getId(), item.getName(), item.getDescription(), item.isActive()))
                    .toList();
            case AUTHORS -> authorRepository.findAll(Sort.by("name")).stream()
                    .map(item -> new ReferenceView(item.getId(), item.getName(), item.getBiography(), true)).toList();
            case PUBLISHERS -> publisherRepository.findAll(Sort.by("name")).stream()
                    .map(item -> new ReferenceView(item.getId(), item.getName(), item.getAddress(), true)).toList();
        };
    }

    @Transactional(readOnly = true)
    public ReferenceForm getForm(ReferenceType type, Long id) {
        ReferenceForm form = new ReferenceForm();
        form.setId(id);
        switch (type) {
            case CATEGORIES -> {
                Category item = categoryRepository.findById(id).orElseThrow();
                form.setName(item.getName());
                form.setDetails(item.getDescription());
                form.setActive(item.isActive());
            }
            case AUTHORS -> {
                Author item = authorRepository.findById(id).orElseThrow();
                form.setName(item.getName());
                form.setDetails(item.getBiography());
            }
            case PUBLISHERS -> {
                Publisher item = publisherRepository.findById(id).orElseThrow();
                form.setName(item.getName());
                form.setDetails(item.getAddress());
            }
        }
        return form;
    }

    @Transactional
    public void save(ReferenceType type, ReferenceForm form) {
        String name = form.getName().trim();
        switch (type) {
            case CATEGORIES -> saveCategory(form, name);
            case AUTHORS -> saveAuthor(form, name);
            case PUBLISHERS -> savePublisher(form, name);
        }
    }

    @Transactional
    public void delete(ReferenceType type, Long id) {
        switch (type) {
            case CATEGORIES -> {
                Category item = categoryRepository.findById(id).orElseThrow();
                if (bookRepository.countByCategoryId(id) > 0) {
                    item.setActive(false);
                } else {
                    categoryRepository.delete(item);
                }
            }
            case AUTHORS -> {
                if (bookRepository.countByAuthorsId(id) > 0) {
                    throw new IllegalStateException("Tác giả đang được gắn với sách");
                }
                authorRepository.deleteById(id);
            }
            case PUBLISHERS -> {
                if (bookRepository.countByPublisherId(id) > 0) {
                    throw new IllegalStateException("Nhà xuất bản đang được gắn với sách");
                }
                publisherRepository.deleteById(id);
            }
        }
    }

    private void saveCategory(ReferenceForm form, String name) {
        categoryRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(form.getId()))
                .ifPresent(existing -> { throw new IllegalArgumentException("Tên thể loại đã tồn tại"); });
        Category item = form.getId() == null
                ? new Category(name, uniqueCategorySlug(name))
                : categoryRepository.findById(form.getId()).orElseThrow();
        item.setName(name);
        item.setDescription(blankToNull(form.getDetails()));
        item.setActive(form.isActive());
        categoryRepository.save(item);
    }

    private void saveAuthor(ReferenceForm form, String name) {
        Author item = form.getId() == null ? new Author(name)
                : authorRepository.findById(form.getId()).orElseThrow();
        item.setName(name);
        item.setBiography(blankToNull(form.getDetails()));
        authorRepository.save(item);
    }

    private void savePublisher(ReferenceForm form, String name) {
        publisherRepository.findByNameIgnoreCase(name)
                .filter(existing -> !existing.getId().equals(form.getId()))
                .ifPresent(existing -> { throw new IllegalArgumentException("Tên nhà xuất bản đã tồn tại"); });
        Publisher item = form.getId() == null ? new Publisher(name)
                : publisherRepository.findById(form.getId()).orElseThrow();
        item.setName(name);
        item.setAddress(blankToNull(form.getDetails()));
        publisherRepository.save(item);
    }

    private String uniqueCategorySlug(String name) {
        String base = Slugifier.toSlug(name);
        String slug = base;
        int suffix = 2;
        while (categoryRepository.existsBySlug(slug)) slug = base + "-" + suffix++;
        return slug;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
