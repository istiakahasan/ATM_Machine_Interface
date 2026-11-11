import java.util.ArrayList;

/**
 * Manages the repository of customer bank accounts and coordinates
 * inter-account transactions and lookups.
 *
 * @author Senior Java Architect
 * @version 1.0
 */
public class Bank {
    private final ArrayList<Account> accounts;

    /**
     * Constructs a Bank instance and populates the master account registry
     * from persistent storage or default seeds via Persistence.
     */
    public Bank() {
        this.accounts = Persistence.loadAccounts();
    }

    /**
     * Registers a new account into the bank repository and persists the updated registry.
     *
     * @param account The Account instance to add to the bank.
     */
    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
            save();
        }
    }

    /**
     * Searches for an account within the registry by user identifier (case-insensitive).
     *
     * @param userId The unique user identifier to look up.
     * @return The matching Account object if found, or null if no match exists.
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
     * Executes an atomic transfer of funds between two distinct accounts.
     * Verifies account validity, prevents self-transfers, checks for non-positive amounts,
     * checks that the source account possesses sufficient funds, and debits the source
     * and credits the destination in an atomic sequence.
     *
     * @param sourceAccount      The sending account to debit.
     * @param destinationAccount The receiving account to credit.
     * @param amount             The monetary amount to transfer.
     * @return true if the transfer was completed successfully; false otherwise.
     */
    public boolean transfer(Account sourceAccount, Account destinationAccount, double amount) {
        if (sourceAccount == null || destinationAccount == null) {
            return false;
        }
        if (sourceAccount.getUserId().equalsIgnoreCase(destinationAccount.getUserId())) {
            return false;
        }
        if (amount <= 0.0 || sourceAccount.getBalance() < amount) {
            return false;
        }

        if (sourceAccount.debit(amount)) {
            destinationAccount.credit(amount);
            save();
            return true;
        }

        return false;
    }

    /**
     * Persists the current state of all registered accounts to persistent file storage.
     */
    public void save() {
        Persistence.saveAccounts(this.accounts);
    }

    /**
     * Retrieves the master list of all customer accounts managed by this bank.
     *
     * @return An ArrayList containing the registered Account instances.
     */
    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
