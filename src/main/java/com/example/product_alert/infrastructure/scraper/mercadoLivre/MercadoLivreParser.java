package com.example.product_alert.infrastructure.scraper.mercadoLivre;

import com.example.product_alert.domain.model.Price;
import com.example.product_alert.domain.model.Product;
import com.example.product_alert.domain.model.Store;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class MercadoLivreParser {

    public List<Product> parse(String html) {
        Document doc = Jsoup.parse(html);
        List<Product> products = new ArrayList<>();

        for (Element productCard : doc.select("div.poly-card")) {

            // pula patrocinados
            if (productCard.selectFirst("a.poly-component__ads-promotions") != null)
                continue;

            Element link = productCard.selectFirst("a.poly-component__title");
            if (link == null)
                continue;

            Price currentPrice = money(productCard.selectFirst(".poly-price__current"));
            if (currentPrice == null)
                continue;

            Element seller = productCard.selectFirst("span.poly-component__seller");

            products.add(new Product(
                    link.text(),
                    currentPrice,
                    money(productCard.selectFirst(".poly-price__labels s")),
                    cleanUrl(link.attr("href")),
                    Store.MERCADO_LIVRE,
                    seller != null ? seller.text().trim() : null,
                    Instant.now())
            );
        }

        return products;
    }

    private Price money(Element element) {
        if (element == null)
            return null;

        Element frac = element.selectFirst(".andes-money-amount__fraction");
        if (frac == null) return null;
        Element cents = element.selectFirst(".andes-money-amount__cents");

        String inteiros = frac.text().replace(".", "");
        String centavos = cents != null ? cents.text() : "00";

        return new Price(new BigDecimal(inteiros + "." + centavos), "BRL");
    }

    private String cleanUrl(String url) {
        int i = url.indexOf("#");
        return i > 0 ? url.substring(0, i) : url;
    }
}
