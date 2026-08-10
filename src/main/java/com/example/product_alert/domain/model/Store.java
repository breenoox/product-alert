package com.example.product_alert.domain.model;

import lombok.Getter;

@Getter
public enum Store {
    MERCADO_LIVRE("Mercado Livre"),
    KABUM("Kabum");

    private final String name;

    Store(String name) {
        this.name = name;
    }
}
