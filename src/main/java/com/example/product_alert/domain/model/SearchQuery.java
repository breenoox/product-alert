package com.example.product_alert.domain.model;

public record SearchQuery(String term, Category category, int maxPages) {

    public SearchQuery {

        if((term == null || term.isBlank()) && (category == null)) {
            throw new IllegalArgumentException("Enter a term or a category");
        }

        if (maxPages < 1) {
            throw new IllegalArgumentException("Max number of pages must be greater than 0");
        }
    }

    public static SearchQuery ofCategory(Category category, int maxPages) {
        return new SearchQuery(null, category, maxPages);
    }

    public static SearchQuery of(String term) {
        return new SearchQuery(term, null, 1);
    }

    public boolean hasCategory() {
        return category != null;
    }
}
