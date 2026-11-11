import java.util.ArrayList;

/**
 * File: Bank.java
 * Responsibility: Manages the master repository of customer accounts.
 * Provides services for account lookup and coordinates cross-account funds transfers.
 */
public class Bank {
    private final ArrayList<Account> accounts;

    public Bank() {
        this.accounts = Persistence.loadAccounts();
    }

    public void addAccount(Account account) {
        if (account != null) {
            accounts.add(account);
            save();
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

    public void save() {
        Persistence.saveAccounts(this.accounts);
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
