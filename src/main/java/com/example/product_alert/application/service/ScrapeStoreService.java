package com.example.product_alert.application.service;

import com.example.product_alert.application.config.ScrapingProperties;
import com.example.product_alert.application.port.out.NotificationSender;
import com.example.product_alert.application.port.out.StoreScraper;
import com.example.product_alert.domain.exception.NotificationException;
import com.example.product_alert.domain.exception.ScrapingException;
import com.example.product_alert.domain.model.Notification;
import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.SearchQuery;
import com.example.product_alert.domain.policy.DiscountPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Queries every registered store, selects the relevant deals and dispatches the
 * notification. An isolated failure — in a single store or in the notification
 * channel — never discards the remaining work.
 */
@Service
public class ScrapeStoreService {

    private static final Logger log = LoggerFactory.getLogger(ScrapeStoreService.class);

    private static final Comparator<Product> BY_PRICE =
            Comparator.comparing(product -> product.price().value());

    private final List<StoreScraper> storeScrapers;
    private final NotificationSender notifier;
    private final DiscountPolicy discountPolicy;
    private final String notificationTitle;

    public ScrapeStoreService(List<StoreScraper> storeScrapers,
                              NotificationSender notifier,
                              ScrapingProperties properties) {
        Objects.requireNonNull(storeScrapers, "storeScrapers is required");
        Objects.requireNonNull(properties, "properties is required");
        if (storeScrapers.isEmpty()) {
            throw new IllegalArgumentException("No StoreScraper registered");
        }
        this.storeScrapers = List.copyOf(storeScrapers);
        this.notifier = Objects.requireNonNull(notifier, "notifier is required");
        this.discountPolicy = new DiscountPolicy(properties.minimumDiscountPercentage());
        this.notificationTitle = properties.notificationTitle();

        log.info("Registered stores: {}", storeNames());
    }

    /** Runs the full cycle: collect, select and notify. */
    public ScrapeResult scrapeAndNotify(SearchQuery query) {
        Objects.requireNonNull(query, "query is required");

        List<Product> collected = deduplicate(collect(query));
        List<Product> deals = collected.stream()
                .filter(discountPolicy::isRelevant)
                .sorted(BY_PRICE)
                .toList();

        log.info("{} products collected, {} with a relevant discount",
                collected.size(), deals.size());

        notify(deals);
        return new ScrapeResult(collected, deals);
    }

    /** Walks through every store. A single failure does not stop the others. */
    private List<Product> collect(SearchQuery query) {
        List<Product> found = new ArrayList<>();

        for (StoreScraper scraper : storeScrapers) {
            String storeName = scraper.store().getName();
            try {
                List<Product> fromStore = scraper.scrape(query);
                log.info("{}: {} products", storeName, fromStore.size());
                found.addAll(fromStore);

            } catch (ScrapingException e) {
                log.warn("Failed to query {}: {}", storeName, e.getMessage());

            } catch (RuntimeException e) {
                log.error("Unexpected error while querying {}", storeName, e);
            }
        }
        return found;
    }

    private void notify(List<Product> deals) {
        if (deals.isEmpty()) {
            log.debug("No relevant deals; notification skipped");
            return;
        }
        try {
            notifier.send(new Notification(notificationTitle, deals));
        } catch (NotificationException e) {
            log.error("Failed to send notification; collected data was preserved", e);
        }
    }

    /** Keeps, for each URL, the cheapest occurrence. */
    private List<Product> deduplicate(List<Product> products) {
        Map<String, Product> byUrl = new LinkedHashMap<>(products.size());

        for (Product product : products) {
            byUrl.merge(product.url(), product,
                    (existing, candidate) ->
                            existing.price().isLessThan(candidate.price()) ? existing : candidate);
        }
        return List.copyOf(byUrl.values());
    }

    private List<String> storeNames() {
        return storeScrapers.stream().map(scraper -> scraper.store().getName()).toList();
    }
}