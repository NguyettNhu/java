package com.yukihira.bookstore.admin.catalog;

public class CatalogValidationException extends IllegalArgumentException {
    private final String field;

    public CatalogValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() { return field; }
}
