package com.Banking.ATM_Machine_Interface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File: Transaction.java
 * Responsibility: Models an immutable record of an individual transaction
 * executed on an account (Deposit, Withdraw, Transfer).
 */
public class Transaction {
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String details;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction(String type, double amount, String details) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-8s | Amount: $%9.2f | %s",
                timestamp.format(FORMATTER),
                type,
                amount,
                details);
    }
}
