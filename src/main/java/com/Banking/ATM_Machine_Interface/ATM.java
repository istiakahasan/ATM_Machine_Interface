package com.Banking.ATM_Machine_Interface;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * File: ATM.java
 * Responsibility: Manages user interface interactions, console authentication,
 * menu navigation, withdrawals, deposits, transfers, and session transaction logging.
 */
public class ATM {
    private static final int MAX_ATTEMPTS = 3;

    private final Bank bank;
    private final Scanner scanner;
    private Account currentAccount;
    private final ArrayList<Transaction> currentSessionHistory;

    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
        this.currentSessionHistory = new ArrayList<>();
        this.currentAccount = null;
    }

    public void start() {
        print("==================================================");
        print("          WELCOME TO THE CONSOLE ATM              ");
        print("==================================================");

        int attempts = 0;
        while (attempts < MAX_ATTEMPTS) {
            if (tryAuthenticate()) {
                return;
            }
            attempts++;
            notifyFailedAttempt(attempts);
        }
    }

    private boolean tryAuthenticate() {
        System.out.print("\nEnter User ID: ");
        String userId = scanner.nextLine().trim();

        System.out.print("Enter PIN: ");
        String pin = scanner.nextLine().trim();

        Account account = bank.findAccount(userId);
        if (account != null && account.validatePin(pin)) {
            print("\nLogin successful! Welcome, " + account.getUserId() + ".");
            this.currentAccount = account;
            this.currentSessionHistory.clear();
            showMenu();
            return true;
        }
        return false;
    }

    private void notifyFailedAttempt(int attempts) {
        int remainingAttempts = MAX_ATTEMPTS - attempts;
        if (remainingAttempts > 0) {
            print("Invalid User ID or PIN. Attempts remaining: " + remainingAttempts);
        } else {
            print("\n[ACCESS DENIED] 3 incorrect attempts. Access locked. Exiting program...");
        }
    }

    public void showMenu() {
        boolean sessionActive = true;
        while (sessionActive) {
            displayMenuBanner();
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            sessionActive = processMenuChoice(input);
        }
    }

    private void displayMenuBanner() {
        print("\n==================================================");
        print("                    MAIN MENU                     ");
        print("==================================================");
        print("1. Transaction History");
        print("2. Withdraw");
        print("3. Deposit");
        print("4. Transfer");
        print("5. Quit");
        print("==================================================");
        System.out.print("Please enter your choice (1-5): ");
    }

    private boolean processMenuChoice(String input) {
        try {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    showHistory();
                    return true;
                case 2:
                    withdraw();
                    return true;
                case 3:
                    deposit();
                    return true;
                case 4:
                    transfer();
                    return true;
                case 5:
                    print("\nThank you for choosing our ATM. Goodbye!");
                    return false;
                default:
                    print("Invalid choice.");
                    return true;
            }
        } catch (NumberFormatException e) {
            print("Invalid choice.");
            return true;
        }
    }

    public void withdraw() {
        print("\n--- WITHDRAW CASH ---");
        printf("Current Balance: $%.2f%n", currentAccount.getBalance());

        double amount = readValidAmount("Enter amount to withdraw: $");
        if (currentAccount.getBalance() < amount) {
            print("Insufficient Funds");
            return;
        }

        if (currentAccount.debit(amount)) {
            bank.save();
            recordTransaction("WITHDRAW", amount, String.format("Cash withdrawal. New balance: $%.2f", currentAccount.getBalance()));
            printf("Withdrawal successful! Dispensed: $%.2f%n", amount);
            printf("Updated Balance: $%.2f%n", currentAccount.getBalance());
        } else {
            print("Insufficient Funds");
        }
    }

    public void deposit() {
        print("\n--- DEPOSIT CASH ---");
        printf("Current Balance: $%.2f%n", currentAccount.getBalance());

        double amount = readValidAmount("Enter amount to deposit: $");
        if (currentAccount.credit(amount)) {
            bank.save();
            recordTransaction("DEPOSIT", amount, String.format("Cash deposit. New balance: $%.2f", currentAccount.getBalance()));
            printf("Deposit successful! Deposited: $%.2f%n", amount);
            printf("Updated Balance: $%.2f%n", currentAccount.getBalance());
        } else {
            print("Error: Deposit failed. Amount must be positive.");
        }
    }

    public void transfer() {
        print("\n--- TRANSFER FUNDS ---");
        printf("Current Balance: $%.2f%n", currentAccount.getBalance());

        Account recipientAccount = resolveRecipient();
        if (recipientAccount == null) {
            return;
        }

        double amount = readValidAmount("Enter amount to transfer: $");
        if (currentAccount.getBalance() < amount) {
            print("Insufficient Funds");
            return;
        }

        if (bank.transfer(currentAccount, recipientAccount, amount)) {
            recordTransaction("TRANSFER", amount, String.format("Transferred to %s. New balance: $%.2f", recipientAccount.getUserId(), currentAccount.getBalance()));
            printf("Transfer successful! Sent $%.2f to %s.%n", amount, recipientAccount.getUserId());
            printf("Updated Balance: $%.2f%n", currentAccount.getBalance());
        } else {
            print("Insufficient Funds");
        }
    }

    private Account resolveRecipient() {
        System.out.print("Enter recipient User ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.isEmpty()) {
            print("Error: Recipient User ID cannot be blank.");
            return null;
        }
        if (recipientId.equalsIgnoreCase(currentAccount.getUserId())) {
            print("Error: You cannot transfer funds to yourself.");
            return null;
        }

        Account recipientAccount = bank.findAccount(recipientId);
        if (recipientAccount == null) {
            print("Error: Recipient account not found.");
        }
        return recipientAccount;
    }

    public void showHistory() {
        print("\n========================================================================================================");
        print("                                  CURRENT SESSION TRANSACTION HISTORY                                   ");
        print("========================================================================================================");
        print("User ID: " + currentAccount.getUserId());
        printf("Current Available Balance: $%.2f%n", currentAccount.getBalance());
        print("--------------------------------------------------------------------------------------------------------");

        if (currentSessionHistory.isEmpty()) {
            print("No transactions in this session.");
        } else {
            renderHistoryTable();
        }
        print("========================================================================================================");
    }

    private void renderHistoryTable() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        printf("%-3s | %-8s | %-12s | %-45s | %-19s%n", "#", "Type", "Amount", "Details", "Time");
        print("----+----------+--------------+-----------------------------------------------+--------------------");

        for (int i = 0; i < currentSessionHistory.size(); i++) {
            Transaction transaction = currentSessionHistory.get(i);
            printf("%-3d | %-8s | $%-11.2f | %-45s | %-19s%n",
                    (i + 1),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getDetails(),
                    transaction.getTimestamp().format(formatter));
        }
    }

    private double readValidAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                double amount = Double.parseDouble(line);
                if (amount <= 0.0) {
                    print("Amount must be positive.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException e) {
                print("Invalid amount. Try again.");
            }
        }
    }

    private void recordTransaction(String type, double amount, String details) {
        currentSessionHistory.add(new Transaction(type, amount, details));
    }

    private void print(String message) {
        System.out.println(message);
    }

    private void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public ArrayList<Transaction> getCurrentSessionHistory() {
        return currentSessionHistory;
    }

    public Bank getBank() {
        return bank;
    }
}
