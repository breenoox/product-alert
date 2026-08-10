package com.example.product_alert.application.service;

import com.example.product_alert.application.port.out.StoreScraper;
import com.example.product_alert.domain.exception.ScrapingException;
import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.SearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScrapeStoreService {

    private static final Logger log = LoggerFactory.getLogger(ScrapeStoreService.class);
    private final List<StoreScraper> storeScrapers;

    public ScrapeStoreService(List<StoreScraper> scrapers) {
        if (scrapers == null || scrapers.isEmpty())
            throw new IllegalArgumentException("scrapers must not be empty");

        this.storeScrapers = scrapers;
    }

    public List<Product> scrapeAll(SearchQuery query) {
        List<Product> products = new ArrayList<>();

        for (StoreScraper storeScraper : storeScrapers) {
            try {
                products.addAll(storeScraper.scrape(query));
            } catch (ScrapingException e) {
                log.info("Falha em %s: %s%n", storeScraper.store().getName(), e.getMessage());
            }
        }
        return deduplicate(products);
    }

    private List<Product> deduplicate(List<Product> products) {
        Map<String, Product> byUrl = new LinkedHashMap<>();

        for (Product product : products) {
            byUrl.merge(product.url(), product, (p1, p2) -> p1.price().isLessThan(p2.price()) ? p1 : p2);
        }

        return List.copyOf(byUrl.values());
    }
}
