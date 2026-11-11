package com.Banking.ATM_Machine_Interface;

import java.util.ArrayList;

/**
 * File: Bank.java
 * Responsibility: Manages the master repository of customer accounts.
 * Provides services for account lookup and coordinates cross-account funds transfers.
 */
public class Bank {
    private final ArrayList<Account> accounts;

    public Bank() {
        this.accounts = new ArrayList<>();
        seedMockAccounts();
    }

    private void seedMockAccounts() {
        accounts.add(new Account("user1", "1234", 1000.00));
        accounts.add(new Account("user2", "5678", 500.00));
        accounts.add(new Account("user3", "9999", 2500.00));
    }

    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
        }
    }

    public Account findAccount(String userId) {
        if (userId == null) {
            return null;
        }
        for (Account account : accounts) {
            if (account.getUserId().equalsIgnoreCase(userId.trim())) {
                return account;
            }
        }
        return null;
    }

    public boolean transfer(Account from, Account to, double amount) {
        if (from == null || to == null) {
            return false;
        }
        if (from.getUserId().equalsIgnoreCase(to.getUserId())) {
            return false;
        }
        if (amount <= 0.0) {
            return false;
        }
        if (from.getBalance() < amount) {
            return false;
        }

        boolean debited = from.debit(amount);
        if (debited) {
            to.credit(amount);
            return true;
        }

        return false;
    }

    public void save() {
        // Optional persistence hook
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
