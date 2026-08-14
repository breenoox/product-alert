package com.example.product_alert.infrastructure.notification.telegram;

import com.example.product_alert.domain.exception.NotificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class TelegramClient {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();
    private final TelegramProperties properties;

    public TelegramClient(TelegramProperties properties) {
        this.properties = properties;
    }

    public void sendMessage(String text) {
        try {
            String body = constructBody(text);
            HttpRequest request = buildRequest(body);

            HttpResponse<String> response =
                    http.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                throw new NotificationException(
                        "Telegram response: " + response.statusCode() + ": " + response.body());

        } catch (Exception e) {
            throw new NotificationException("Failed to send message", e);
        }
    }

    private String constructBody(String text) {
        try {
            return mapper.writeValueAsString(Map.of(
                    "chat_id", properties.chatId(),
                    "text", text,
                    "parse_mode", "MarkdownV2",
                    "disable_web_page_preview", true
            ));
        } catch (Exception e) {
            throw new NotificationException("Failed to build message body", e);
        }
    }

    private HttpRequest buildRequest(String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(properties.sendMessageUrl()))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
