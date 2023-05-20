package com.pro100svitlo.creditCardNfcReader.enums;

public enum CardPaymentType {
    VISA("Visa"),
    MASTER_CARD("MasterCard"),
    AMERICAN_EXPRESS("American Express"),
    DISCOVER("Discover"),
    UNKNOWN("Unknown");

    private final String name;

    CardPaymentType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
