package com.example.product_alert.domain.policy;

import com.example.product_alert.domain.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Optional;

public final class DiscountPolicy {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int PERCENTAGE_SCALE = 4;

    private final BigDecimal minimumPercentage;

    public DiscountPolicy(BigDecimal minimumPercentage) {
        this.minimumPercentage =
                Objects.requireNonNull(minimumPercentage, "minimumPercentage is required");
        if (minimumPercentage.signum() < 0) {
            throw new IllegalArgumentException("minimumPercentage must not be negative");
        }
    }

    public boolean isRelevant(Product product) {
        return discountPercentage(product)
                .filter(percentage -> percentage.compareTo(minimumPercentage) >= 0)
                .isPresent();
    }

    public Optional<BigDecimal> discountPercentage(Product product) {
        if (product.oldPrice() == null) {
            return Optional.empty();
        }
        BigDecimal previous = product.oldPrice().value();
        if (previous.signum() <= 0) {
            return Optional.empty();
        }
        BigDecimal current = product.price().value();
        if (current.compareTo(previous) >= 0) {
            return Optional.of(BigDecimal.ZERO);
        }
        return Optional.of(previous.subtract(current)
                .multiply(HUNDRED)
                .divide(previous, PERCENTAGE_SCALE, RoundingMode.HALF_UP));
    }
}
