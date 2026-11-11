package com.Banking.ATM_Machine_Interface;

/**
 * File: Account.java
 * Responsibility: Represents an individual bank customer's account entity.
 * Encapsulates credentials (userId, pin) and balance state, providing
 * controlled debit and credit operations.
 */
public class Account {
    private final String userId;
    private final String pin;
    private double balance;

    public Account(String userId, String pin, double balance) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (pin == null || pin.trim().isEmpty()) {
            throw new IllegalArgumentException("PIN cannot be null or empty.");
        }
        if (balance < 0.0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }
        this.userId = userId.trim();
        this.pin = pin.trim();
        this.balance = balance;
    }

    public String getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public boolean validatePin(String enteredPin) {
        if (enteredPin == null) {
            return false;
        }
        return this.pin.equals(enteredPin.trim());
    }

    public boolean debit(double amount) {
        if (amount <= 0.0 || amount > balance) {
            return false;
        }
        this.balance -= amount;
        return true;
    }

    public boolean credit(double amount) {
        if (amount <= 0.0) {
            return false;
        }
        this.balance += amount;
        return true;
    }
}
