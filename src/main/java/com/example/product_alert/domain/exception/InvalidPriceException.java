package com.example.product_alert.domain.exception;

public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException() {
        super("Invalid price");
    }
}
