package com.example.product_alert.interfaces.scheduler;

import com.example.product_alert.application.service.ScrapeStoreService;
import com.example.product_alert.domain.model.Category;
import com.example.product_alert.domain.model.SearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScrapingScheduler {

    private static final Logger log = LoggerFactory.getLogger(ScrapingScheduler.class);

    private final ScrapeStoreService service;
    private final Category category;
    private final int maxPages;

    public ScrapingScheduler(ScrapeStoreService service,
                             @Value("${scraper.category}") Category category,
                             @Value("${scraper.max-pages}") int maxPages) {
        this.service = service;
        this.category = category;
        this.maxPages = maxPages;
    }

    @Scheduled(cron = "${scraper.cron}")
    public void executar() {
        log.info("Iniciando scraping da categoria {}", category);
        var produtos = service.scrapeAndNotify(SearchQuery.ofCategory(category, maxPages));
    }
}