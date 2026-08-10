package com.example.product_alert.infrastructure.scraper.mercadoLivre;

import com.example.product_alert.application.port.out.HtmlFetcher;
import com.example.product_alert.application.port.out.StoreScraper;
import com.example.product_alert.domain.exception.ScrapingException;
import com.example.product_alert.domain.model.Category;
import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.SearchQuery;
import com.example.product_alert.domain.model.Store;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MercadoLivreScraper implements StoreScraper {

    private static final String OFFERS = "https://www.mercadolivre.com.br/ofertas";
    private static final String SEARCH   = "https://lista.mercadolivre.com.br/";
    private static final Map<Category, String> CODES = Map.of(
            Category.COMPUTING, "MLB1648"
    );

    private final HtmlFetcher fetcher;
    private final MercadoLivreParser parser;

    public MercadoLivreScraper(
            HtmlFetcher fetcher,
            MercadoLivreParser parser
    ) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    @Override
    public Store store() {
        return Store.MERCADO_LIVRE;
    }

    @Override
    public List<Product> scrape(SearchQuery query) {
        List<Product> products = new ArrayList<>();

        for(int page = 1; page <= query.maxPages(); page++){
            String html = fetcher.fetch(buildUrl(query, page));
            List<Product> ofPage = parser.parse(html);

            if (ofPage.isEmpty()) break;
            products.addAll(ofPage);

            pause();
        }

        return products;
    }

    private String buildUrl(SearchQuery query, int page) {
        if (query.hasCategory()) {
            return OFFERS + "?category=" + CODES.get(query.category()) + "&page=" + page;
        }
        return SEARCH + URLEncoder.encode(query.term(), StandardCharsets.UTF_8);
    }

    private void pause() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScrapingException("Scraping interrompido", e);
        }
    }
}