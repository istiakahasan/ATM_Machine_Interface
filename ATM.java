import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * File: ATM.java
 * Responsibility: Manages user interface interactions, console input/output,
 * authentication control, and coordinates banking transactions for the current
 * session.
 */
public class ATM {
    // Member fields
    private final Bank bank;
    private final Scanner scanner;
    private Account currentAccount;
    private final ArrayList<Transaction> currentSessionHistory;

    /**
     * Constructs an ATM interface tied to a specific Bank instance.
     *
     * @param bank The banking repository and operations provider
     */
    public ATM(Bank bank) {
        this.bank = bank;
        this.scanner = new Scanner(System.in);
        this.currentSessionHistory = new ArrayList<>();
        this.currentAccount = null;
    }

    /**
     * Initiates the ATM interaction loop.
     * Enforces authentication with a 3-attempt lockout mechanism.
     */
    public void start() {
        System.out.println("==================================================");
        System.out.println("          WELCOME TO THE CONSOLE ATM              ");
        System.out.println("==================================================");

        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            System.out.print("\nEnter User ID: ");
            String userId = scanner.nextLine().trim();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.findAccount(userId);

            if (account != null && account.validatePin(pin)) {
                System.out.println("\nAuthentication Successful! Welcome, " + account.getUserId() + ".");
                this.currentAccount = account;
                this.currentSessionHistory.clear(); // Reset session history on successful login
                showMenu();
                return;
            } else {
                attempts++;
                int remaining = MAX_ATTEMPTS - attempts;
                if (remaining > 0) {
                    System.out.println("Invalid User ID or PIN. Attempts remaining: " + remaining);
                } else {
                    System.out.println("\n[ACCESS DENIED] 3 incorrect attempts. Access locked. Exiting program...");
                    return;
                }
            }
        }
    }

    /**
     * Displays the primary menu and processes user actions via switch-case.
     */
    public void showMenu() {
        boolean sessionActive = true;

        while (sessionActive) {
            System.out.println("\n==================================================");
            System.out.println("                    MAIN MENU                     ");
            System.out.println("==================================================");
            System.out.println("1. Transaction History");
            System.out.println("2. Withdraw");
            System.out.println("3. Deposit");
            System.out.println("4. Transfer");
            System.out.println("5. Quit");
            System.out.println("==================================================");
            System.out.print("Please enter your choice (1-5): ");

            String input = scanner.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number from 1 to 5.");
                continue;
            }

            switch (choice) {
                case 1:
                    showHistory();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    deposit();
                    break;
                case 4:
                    transfer();
                    break;
                case 5:
                    System.out.println("\nThank you for choosing our ATM. Goodbye!");
                    sessionActive = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select an option between 1 and 5.");
                    break;
            }
        }
    }

    /**
     * Handles cash withdrawal with balance check and session logging.
     */
    public void withdraw() {
        System.out.println("\n--- WITHDRAW CASH ---");
        System.out.printf("Current Balance: $%.2f%n", currentAccount.getBalance());

        double amount = readValidAmount("Enter amount to withdraw: $");

        // Rule 4 & 5: Check balance BEFORE withdrawal and display EXACT phrase
        if (currentAccount.getBalance() < amount) {
            System.out.println("Insufficient Funds");
            return;
        }

        boolean success = currentAccount.debit(amount);
        if (success) {
            Transaction tx = new Transaction("WITHDRAW", amount,
                    String.format("Cash withdrawal. New balance: $%.2f", currentAccount.getBalance()));
            currentSessionHistory.add(tx);
            System.out.printf("Withdrawal successful! Dispensed: $%.2f%n", amount);
            System.out.printf("Updated Balance: $%.2f%n", currentAccount.getBalance());
        } else {
            System.out.println("Insufficient Funds");
        }
    }

    /**
     * Handles cash deposit and session logging.
     */
    public void deposit() {
        System.out.println("\n--- DEPOSIT CASH ---");
        System.out.printf("Current Balance: $%.2f%n", currentAccount.getBalance());

        double amount = readValidAmount("Enter amount to deposit: $");

        boolean success = currentAccount.credit(amount);
        if (success) {
            Transaction tx = new Transaction("DEPOSIT", amount,
                    String.format("Cash deposit. New balance: $%.2f", currentAccount.getBalance()));
            currentSessionHistory.add(tx);
            System.out.printf("Deposit successful! Deposited: $%.2f%n", amount);
            System.out.printf("Updated Balance: $%.2f%n", currentAccount.getBalance());
        } else {
            System.out.println("Error: Deposit failed. Amount must be positive.");
        }
    }

    /**
     * Handles funds transfer to another account with balance and recipient
     * validation.
     */
    public void transfer() {
        System.out.println("\n--- TRANSFER FUNDS ---");
        System.out.printf("Current Balance: $%.2f%n", currentAccount.getBalance());

        System.out.print("Enter recipient User ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.isEmpty()) {
            System.out.println("Error: Recipient User ID cannot be blank.");
            return;
        }

        if (recipientId.equalsIgnoreCase(currentAccount.getUserId())) {
            System.out.println("Error: You cannot transfer funds to yourself.");
            return;
        }

        Account recipientAccount = bank.findAccount(recipientId);
        if (recipientAccount == null) {
            System.out.println("Error: Recipient account not found.");
            return;
        }

        double amount = readValidAmount("Enter amount to transfer: $");

        // Rule 4 & 5: Check balance BEFORE transfer and display EXACT phrase
        if (currentAccount.getBalance() < amount) {
            System.out.println("Insufficient Funds");
            return;
        }

        boolean success = bank.transfer(currentAccount, recipientAccount, amount);
        if (success) {
            Transaction tx = new Transaction("TRANSFER", amount,
                    String.format("Transferred to %s. New balance: $%.2f", recipientAccount.getUserId(),
                            currentAccount.getBalance()));
            currentSessionHistory.add(tx);
            System.out.printf("Transfer successful! Sent $%.2f to %s.%n", amount, recipientAccount.getUserId());
            System.out.printf("Updated Balance: $%.2f%n", currentAccount.getBalance());
        } else {
            System.out.println("Insufficient Funds");
        }
    }

    /**
     * Displays the formatted transaction history for the current session.
     * Renders a clean tabular layout with columns: # | Type | Amount | Details | Time.
     * If no transactions exist, prints "No transactions in this session."
     */
    public void showHistory() {
        System.out.println("\n========================================================================================================");
        System.out.println("                                  CURRENT SESSION TRANSACTION HISTORY                                   ");
        System.out.println("========================================================================================================");
        System.out.println("User ID: " + currentAccount.getUserId());
        System.out.printf("Current Available Balance: $%.2f%n", currentAccount.getBalance());
        System.out.println("--------------------------------------------------------------------------------------------------------");

        if (currentSessionHistory.isEmpty()) {
            System.out.println("No transactions in this session.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.printf("%-3s | %-8s | %-12s | %-45s | %-19s%n", "#", "Type", "Amount", "Details", "Time");
            System.out.println("----+----------+--------------+-----------------------------------------------+--------------------");

            for (int i = 0; i < currentSessionHistory.size(); i++) {
                Transaction tx = currentSessionHistory.get(i);
                System.out.printf("%-3d | %-8s | $%-11.2f | %-45s | %-19s%n",
                        (i + 1),
                        tx.getType(),
                        tx.getAmount(),
                        tx.getDetails(),
                        tx.getTimestamp().format(formatter));
            }
        }
        System.out.println("========================================================================================================");
    }

    /**
     * Safely reads and validates numeric currency inputs from the console.
     * Explicitly rejects non-numeric inputs, negative amounts, zero amounts,
     * and blank strings with user-friendly messages without crashing.
     *
     * @param prompt The prompt to display to the user
     * @return A valid strictly positive double value (> 0.0)
     */
    private double readValidAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("Error: Input cannot be blank. Please enter an amount.");
                continue;
            }

            try {
                double amount = Double.parseDouble(line);

                if (amount < 0.0) {
                    System.out.println("Error: Amount cannot be negative. Please enter a positive amount.");
                    continue;
                } else if (amount == 0.0) {
                    System.out.println("Error: Amount must be greater than zero.");
                    continue;
                }

                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid numeric input. Please enter a valid number (e.g., 50.00).");
            }
        }
    }
}
