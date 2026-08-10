package com.example.product_alert.domain.model;

import com.example.product_alert.domain.exception.InvalidPriceException;
import com.example.product_alert.domain.exception.InvalidProductName;

import java.time.Instant;

public record Product(
        String name,
        Price price,
        Price oldPrice,
        String url,
        Store store,
        String seller,
        Instant captureAt) {

    public Product {
        if (name == null || name.isBlank())
            throw new InvalidProductName();

        if (price == null)
            throw new InvalidPriceException();
    }
}
