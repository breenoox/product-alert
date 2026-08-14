package com.example.product_alert.application.service;

import com.example.product_alert.domain.model.Product;

import java.util.List;

public record ScrapeResult(List<Product> collected, List<Product> deals) {

    public ScrapeResult {
        collected = List.copyOf(collected);
        deals = List.copyOf(deals);
    }

    public static ScrapeResult empty() {
        return new ScrapeResult(List.of(), List.of());
    }
}
