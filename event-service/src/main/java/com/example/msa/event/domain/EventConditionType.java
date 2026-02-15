package com.example.msa.event.domain;

import java.util.Locale;

public enum EventConditionType {
    BIRTHDAY,
    PRODUCT,
    MIN_AMOUNT;

    public static EventConditionType from(String value) {
        return EventConditionType.valueOf(value.toUpperCase(Locale.ROOT));
    }
}
