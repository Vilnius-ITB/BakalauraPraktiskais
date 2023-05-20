package io.github.VilniusITB.BakalauraPraktiskais.enums;

public enum TerminalTransactionStatus {
    PENDING, CANCELED, EXPIRED_CARD, INVALID_PIN, SUCCESS, UNKNOWN;

    public static TerminalTransactionStatus parse(String input) {
        for (TerminalTransactionStatus status : TerminalTransactionStatus.values()) if (status.name().equalsIgnoreCase(input)) return status;
        return UNKNOWN;
    }
}
