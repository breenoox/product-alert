package com.example.product_alert.infrastructure.notification.telegram;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(String botToken, String chatId) {

    public String sendMessageUrl() {
        return "https://api.telegram.org/bot" + botToken + "/sendMessage";
    }
}
