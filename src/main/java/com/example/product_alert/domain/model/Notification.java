package com.example.product_alert.domain.model;

import java.util.List;

public record Notification(String title, List<Product> products) {

    public Notification {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Mandatory title");
        if (products == null || products.isEmpty())
            throw new IllegalArgumentException("nothing to report");

        products = List.copyOf(products);
    }
}
