package com.yukihira.bookstore.admin.report;

import java.math.BigDecimal;

public record TopSellingBookView(Long bookId, String title, Long quantitySold, BigDecimal revenue) {
}
