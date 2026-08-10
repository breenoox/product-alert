package com.example.product_alert.infrastructure.scraper.kabum;

import com.example.product_alert.application.port.out.HtmlFetcher;
import com.example.product_alert.application.port.out.StoreScraper;
import com.example.product_alert.domain.model.Category;
import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.SearchQuery;
import com.example.product_alert.domain.model.Store;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class KabumScraper implements StoreScraper {

    private final HtmlFetcher fetcher;
    private final KabumParser parser;

    private static final String OFFERS = "https://www.kabum.com.br/ofertas";
    private static final Map<Category, String> CODES = Map.of(
            Category.COMPUTING, "ofertaskabum"
    );

    public KabumScraper(
            HtmlFetcher fetcher,
            KabumParser parser

    ) {
        this.fetcher = fetcher;
        this.parser = parser;
    }

    @Override
    public List<Product> scrape(SearchQuery query) {
        List<Product> products = new ArrayList<>();

        for (int page = 1; page <= query.maxPages(); page++) {
            String url = buildUrl(query) + "?pagina=" + page + "&limite=100&tipo=ativas";
            List<Product> foundProducts = parser.parse(fetcher.fetch(url));

            if (foundProducts.isEmpty()) break;
            products.addAll(foundProducts);
        }

        return products;
    }

    private String buildUrl(SearchQuery query) {
        if (!query.hasCategory()) {
            throw new IllegalArgumentException("KabumScraper requires a category to build the URL.");
        }
        return OFFERS + "/" + CODES.get(query.category());
    }

    @Override
    public Store store() {
        return Store.KABUM;
    }
}
