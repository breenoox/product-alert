package com.example.product_alert.domain.model;

import com.example.product_alert.domain.exception.InvalidPriceException;

import java.math.BigDecimal;

public record Price(BigDecimal value, String currency) {
    public Price {
        if (value == null || value.signum() < 0)
            throw new InvalidPriceException();
    }

    public boolean isLessThan(Price other) {
        return value.compareTo(other.value) < 0;
    }
}
