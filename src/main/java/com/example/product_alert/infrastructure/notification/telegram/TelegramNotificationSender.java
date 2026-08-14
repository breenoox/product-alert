package com.example.product_alert.infrastructure.notification.telegram;

import com.example.product_alert.application.port.out.NotificationSender;
import com.example.product_alert.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class TelegramNotificationSender implements NotificationSender {

    private final TelegramClient client;
    private final TelegramMessageFormatter formatter;

    public TelegramNotificationSender(
            TelegramClient client,
            TelegramMessageFormatter formatter)
    {
        this.client = client;
        this.formatter = formatter;
    }

    @Override
    public void send(Notification notification) {
        client.sendMessage(formatter.format(notification));
    }
}
