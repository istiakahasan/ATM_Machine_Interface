/**
 * File: Account.java
 * Responsibility: Represents an individual bank customer's account entity.
 * Encapsulates credentials (userId, pin) and balance state, providing
 * controlled debit and credit operations.
 */
public class Account {
    // Encapsulated private attributes
    private final String userId;
    private final String pin;
    private double balance;

    /**
     * Parameterized constructor to initialize account state.
     *
     * @param userId  Unique customer identifier
     * @param pin     Secret 4-digit PIN for authentication
     * @param balance Initial opening balance
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
     * Retrieves the User ID.
     *
     * @return String user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Retrieves the account PIN (used for persistence serialization).
     *
     * @return String PIN
     */
    public String getPin() {
        return pin;
    }

    /**
     * Retrieves the current balance.
     * Note: No public setter is provided for balance to guarantee data integrity.
     * Balance modifications are only permitted through debit() and credit().
     *
     * @return current double balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Validates whether the provided PIN matches this account's PIN.
     *
     * @param enteredPin The PIN string entered by the user
     * @return true if credentials match; false otherwise
     */
    public boolean validatePin(String enteredPin) {
        if (enteredPin == null) {
            return false;
        }
        return this.pin.equals(enteredPin.trim());
    }

    /**
     * Deducts funds from the account if amount is positive and balance is sufficient.
     *
     * @param amount The double value to deduct
     * @return true if debit was successful; false if insufficient funds or invalid amount
     */
    public boolean debit(double amount) {
        if (amount <= 0.0 || amount > balance) {
            return false;
        }
        this.balance -= amount;
        return true;
    }

    /**
     * Deposits funds into the account if amount is positive.
     *
     * @param amount The double value to add
     * @return true if credit was successful; false if amount is non-positive
     */
    public boolean credit(double amount) {
        if (amount <= 0.0) {
            return false;
        }
        this.balance += amount;
        return true;
    }
}
