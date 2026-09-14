package com.yukihira.bookstore.api;

import org.springframework.data.domain.Page;

import java.util.List;

public record ApiPage<T>(List<T> content, int page, int size, long totalElements,
                         int totalPages, boolean first, boolean last) {

    public static <T> ApiPage<T> from(Page<T> source) {
        return new ApiPage<>(source.getContent(), source.getNumber(), source.getSize(),
                source.getTotalElements(), source.getTotalPages(), source.isFirst(), source.isLast());
    }
}
