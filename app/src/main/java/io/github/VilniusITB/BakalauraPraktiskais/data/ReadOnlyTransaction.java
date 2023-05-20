package io.github.VilniusITB.BakalauraPraktiskais.data;

import java.util.UUID;

import io.github.VilniusITB.BakalauraPraktiskais.enums.TerminalTransactionStatus;

public class ReadOnlyTransaction {
    private final UUID transactionsID;
    private final long startEpochTime;
    private final long endEpochTime;
    private final TerminalTransactionStatus status;
    private final double amount;

    public ReadOnlyTransaction(final UUID transactionsID, long startEpochTime, long endEpochTime, TerminalTransactionStatus status,double amount) {
        this.transactionsID = transactionsID;
        this.startEpochTime = startEpochTime;
        this.endEpochTime = endEpochTime;
        this.status = status;
        this.amount = amount;
    }

    public UUID getTransactionsID() {
        return this.transactionsID;
    }

    public long getStartEpochTime() {
        return this.startEpochTime;
    }

    public long getEndEpochTime() {
        return this.endEpochTime;
    }

    public TerminalTransactionStatus getStatus() {
        return this.status;
    }
    public double getAmount() {
        return this.amount;
    }
}
