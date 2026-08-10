package com.example.product_alert.infrastructure.http;

import com.example.product_alert.application.port.out.HtmlFetcher;
import com.example.product_alert.domain.exception.ScrapingException;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsoupHtmlFetcher implements HtmlFetcher {
    public String fetch(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .header("Accept-Language", "pt-BR,pt;q=0.9")
                    .timeout(10 * 1000)
                    .get()
                    .html();

        } catch (IOException e) {
            throw new ScrapingException("Falha ao buscar HTML de " + url, e);
        }
    }
}
