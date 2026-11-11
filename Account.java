/**
 * Represents an individual bank customer's account entity.
 * Encapsulates credentials (User ID and PIN) and financial balance,
 * protecting internal state through controlled debit and credit operations.
 *
 * @author Senior Java Architect
 * @version 1.0
 */
public class Account {
    private final String userId;
    private final String pin;
    private double balance;

    /**
     * Constructs a new Account with the specified identifier, authentication PIN, and initial opening balance.
     *
     * @param userId  The unique identifier for the account owner; cannot be null or empty.
     * @param pin     The secret authentication PIN string; cannot be null or empty.
     * @param balance The initial balance of the account; must be non-negative.
     * @throws IllegalArgumentException If userId or pin are null/empty, or if balance is negative.
     */
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

    /**
     * Retrieves the unique identifier associated with this account.
     *
     * @return The user identifier string.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Retrieves the secret authentication PIN (used for persistence serialization).
     *
     * @return The account's PIN string.
     */
    public String getPin() {
        return pin;
    }

    /**
     * Retrieves the current available balance of the account.
     * To preserve data encapsulation, no public setter is provided.
     *
     * @return The current monetary balance as a double.
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Validates whether the supplied candidate PIN matches this account's secret PIN.
     *
     * @param enteredPin The candidate PIN entered by the user.
     * @return true if credentials match exactly; false otherwise.
     */
    public boolean validatePin(String enteredPin) {
        if (enteredPin == null) {
            return false;
        }
        return this.pin.equals(enteredPin.trim());
    }

    /**
     * Deducts a positive monetary amount from the account balance if funds are sufficient.
     *
     * @param amount The monetary amount to withdraw or debit; must be strictly positive.
     * @return true if the debit operation succeeded; false if amount exceeds balance or is non-positive.
     */
    public boolean debit(double amount) {
        if (amount <= 0.0 || amount > balance) {
            return false;
        }
        this.balance -= amount;
        return true;
    }

    /**
     * Credits a positive monetary amount to the account balance.
     *
     * @param amount The monetary amount to deposit or credit; must be strictly positive.
     * @return true if the credit operation succeeded; false if amount is non-positive.
     */
    public boolean credit(double amount) {
        if (amount <= 0.0) {
            return false;
        }
        this.balance += amount;
        return true;
    }
}
