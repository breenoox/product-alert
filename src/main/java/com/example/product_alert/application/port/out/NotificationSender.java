package com.example.product_alert.application.port.out;

import com.example.product_alert.domain.model.Notification;

public interface NotificationSender {
    void send(Notification notification);
}
