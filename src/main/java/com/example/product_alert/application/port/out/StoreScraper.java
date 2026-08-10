package com.example.product_alert.application.port.out;

import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.SearchQuery;
import com.example.product_alert.domain.model.Store;

import java.util.List;

public interface StoreScraper {
    Store store();
    List<Product> scrape(SearchQuery query);
}
