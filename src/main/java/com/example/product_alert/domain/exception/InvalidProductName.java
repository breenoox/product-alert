package com.example.product_alert.domain.exception;

public class InvalidProductName extends RuntimeException {
    public InvalidProductName() {
        super("Invalid Product Name");
    }
}
