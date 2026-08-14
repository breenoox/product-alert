package com.example.product_alert.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "product-alert.scraping")
public record ScrapingProperties(
        BigDecimal minimumDiscountPercentage,
        String notificationTitle) {

    private static final BigDecimal DEFAULT_MINIMUM_DISCOUNT = new BigDecimal("30");
    private static final String DEFAULT_NOTIFICATION_TITLE = "Ofertas do dia";

    public ScrapingProperties {
        if (minimumDiscountPercentage == null) {
            minimumDiscountPercentage = DEFAULT_MINIMUM_DISCOUNT;
        }
        if (notificationTitle == null || notificationTitle.isBlank()) {
            notificationTitle = DEFAULT_NOTIFICATION_TITLE;
        }
    }
}
