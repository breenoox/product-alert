package com.example.product_alert.domain.exception;

public class NotificationException extends RuntimeException {
    public NotificationException(String message) { super(message); }
    public NotificationException(String message, Throwable cause) { super(message, cause); }
}
