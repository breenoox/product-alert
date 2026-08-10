package com.example.product_alert.infrastructure.scraper.kabum;

import com.example.product_alert.domain.exception.ScrapingException;
import com.example.product_alert.domain.model.Price;
import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.Store;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class KabumParser {

    private static final String PRODUCT_URL = "https://www.kabum.com.br/produto/";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Product> parse(String html) {
        Element script = Jsoup.parse(html).selectFirst("script#__NEXT_DATA__");
        if (script == null) {
            throw new ScrapingException("Failed to find the script tag with id __NEXT_DATA__");
        }

        List<Product> products = new ArrayList<>();
        try {
            JsonNode items = mapper.readTree(script.data())
                    .path("props")
                    .path("pageProps")
                    .path("dataOfferServer")
                    .path("data");

            for (JsonNode item : items) {
                if(!item.path("available").asBoolean()) continue;

                Price current = money(item.path("price"));
                if (current == null) continue;

                products.add(new Product(
                        item.path("name").asText(),
                        current,
                        oldPrice(item, current),
                        buildUrl(item),
                        Store.KABUM,
                        item.path("sellerName").asText(null),
                        Instant.now())
                );
            }


        } catch (Exception e) {
            throw new ScrapingException(e.getMessage());
        }

        return products;
    }

    private Price money(JsonNode node) {
        if (node.isMissingNode() || node.decimalValue().signum() <= 0) return null;
        return new Price(node.decimalValue(), "BRL");
    }

    private Price oldPrice(JsonNode item, Price current) {
        Price old = money(item.path("oldPrice"));
        if (old == null) return null;
        return current.isLessThan(old) ? old : null;
    }

    private String buildUrl(JsonNode item) {
        return PRODUCT_URL + item.path("code").asLong() + "/" + item.path("friendlyName").asText();
    }
}
