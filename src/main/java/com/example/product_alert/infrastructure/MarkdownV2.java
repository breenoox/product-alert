package com.example.product_alert.infrastructure;

import java.util.regex.Pattern;

public final class MarkdownV2 {

    private static final Pattern RESERVED_CHARS =
            Pattern.compile("[_*\\[\\]()~`>#+\\-=|{}.!\\\\]");

    private static final Pattern RESERVED_URL_CHARS = Pattern.compile("[)\\\\]");

    private static final String ESCAPE_REPLACEMENT = "\\\\$0";

    private MarkdownV2() {
        throw new AssertionError("\n" + "Utility class should not be instantiated.");
    }

    public static String escape(String text) {
        return text == null ? "" : RESERVED_CHARS.matcher(text).replaceAll(ESCAPE_REPLACEMENT);
    }

    public static String escapeUrl(String url) {
        return url == null ? "" : RESERVED_URL_CHARS.matcher(url).replaceAll(ESCAPE_REPLACEMENT);
    }

    public static String bold(String text) {
        return "*" + escape(text) + "*";
    }

    public static String italic(String text) {
        return "_" + escape(text) + "_";
    }

    public static String strikethrough(String text) {
        return "~" + escape(text) + "~";
    }

    public static String link(String label, String url) {
        return "[" + escape(label) + "](" + escapeUrl(url) + ")";
    }
}