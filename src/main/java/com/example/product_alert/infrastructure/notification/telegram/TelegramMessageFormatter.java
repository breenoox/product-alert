package com.example.product_alert.infrastructure.notification.telegram;

import com.example.product_alert.domain.model.Notification;
import com.example.product_alert.domain.model.Product;

import com.example.product_alert.infrastructure.MarkdownV2;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

@Component
public class TelegramMessageFormatter {

    private static final int MESSAGE_MAX_LENGTH = 4_096;
    private static final int PRODUCT_NAME_MAX_LENGTH = 70;
    private static final String ELLIPSIS = "...";
    private static final String PRODUCT_MARKER = "🛒";
    private static final Locale MONEY_LOCALE = Locale.forLanguageTag("pt-BR");

    private static final String OVERFLOW_NOTICE =
            "\n" + MarkdownV2.italic("...");

    public String format(Notification notification) {
        Objects.requireNonNull(notification, "notification cannot be null");

        NumberFormat money = NumberFormat.getCurrencyInstance(MONEY_LOCALE);
        int budget = MESSAGE_MAX_LENGTH - OVERFLOW_NOTICE.length();

        StringBuilder message = new StringBuilder(MarkdownV2.bold(notification.title()))
                .append("\n\n");

        for (Product product : notification.products()) {
            String block = renderProduct(product, money);

            if (message.length() + block.length() > budget) {
                message.append(OVERFLOW_NOTICE);
                break;
            }
            message.append(block);
        }
        return message.toString();
    }

    private String renderProduct(Product product, NumberFormat money) {
        StringBuilder block = new StringBuilder()
                .append(PRODUCT_MARKER).append(' ')
                .append(MarkdownV2.link(truncate(product.name()), String.valueOf(product.url())))
                .append('\n');

        if (product.oldPrice() != null) {
            block.append(MarkdownV2.strikethrough(money.format(product.oldPrice().value())))
                    .append(" → ");
        }

        return block.append(MarkdownV2.bold(money.format(product.price().value())))
                .append("  ")
                .append(MarkdownV2.italic(product.store().getName()))
                .append("\n\n")
                .toString();
    }

    private String truncate(String name) {
        if (name == null || name.length() <= PRODUCT_NAME_MAX_LENGTH) {
            return name;
        }
        return name.substring(0, PRODUCT_NAME_MAX_LENGTH - ELLIPSIS.length()) + ELLIPSIS;
    }
}