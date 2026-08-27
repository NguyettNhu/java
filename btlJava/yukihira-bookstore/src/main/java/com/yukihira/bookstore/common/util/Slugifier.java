package com.yukihira.bookstore.common.util;

import java.text.Normalizer;
import java.util.Locale;

public final class Slugifier {

    private Slugifier() {
    }

    public static String toSlug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .replaceAll("\\p{M}+", "");
        return normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
