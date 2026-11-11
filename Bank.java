import java.util.ArrayList;

/**
 * File: Bank.java
 * Responsibility: Manages the master repository of customer accounts.
 * Provides services for account lookup and coordinates cross-account funds transfers.
 */
public class Bank {
    // Master collection of accounts
    private final ArrayList<Account> accounts;

    /**
     * Constructs a Bank instance and populates it with 3 seeded mock accounts.
     */
    public Bank() {
        this.accounts = Persistence.loadAccounts();
    }

    /**
     * Seeds initial mock accounts for testing and demonstration.
     */
    private void seedMockAccounts() {
        accounts.add(new Account("user1", "1234", 1000.00));
        accounts.add(new Account("user2", "5678", 500.00));
        accounts.add(new Account("user3", "9999", 2500.00));
    }

    /**
     * Registers a new account into the bank repository.
     *
     * @param account The account to be added
     */
    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
        }
    }

    /**
     * Searches for an account by user identifier (case-insensitive).
     *
     * @param userId The unique user identifier
     * @return The Account if found, or null if no matching account exists
     */
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

    /**
     * Transfers funds between two accounts.
     * Performs prerequisite validations: existence, distinct accounts, positive amount,
     * and balance verification prior to executing atomic debit and credit.
     *
     * @param from   Source account to debit
     * @param to     Destination account to credit
     * @param amount Amount to transfer
     * @return true if transfer was completely successful; false otherwise
     */
    public boolean transfer(Account from, Account to, double amount) {
        // Guard checks for null accounts
        if (from == null || to == null) {
            return false;
        }

        // Prevent self-transfer
        if (from.getUserId().equalsIgnoreCase(to.getUserId())) {
            return false;
        }

        // Validate positive amount
        if (amount <= 0.0) {
            return false;
        }

        // Strict balance check BEFORE any debiting occurs
        if (from.getBalance() < amount) {
            return false;
        }

        // Execute atomic debit from source and credit to recipient
        boolean debited = from.debit(amount);
        if (debited) {
            to.credit(amount);
            save();
            return true;
        }

        return false;
    }

    /**
     * Persists accounts to accounts.txt via Persistence helper.
     */
    public void save() {
        Persistence.saveAccounts(this.accounts);
    }

    /**
     * Retrieves the list of accounts managed by this bank.
     *
     * @return ArrayList of accounts
     */
    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
