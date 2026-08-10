package com.example.product_alert.domain.model;

public enum Store {
    MERCADO_LIVRE("Mercado Livre");

    private final String name;
    Store(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
