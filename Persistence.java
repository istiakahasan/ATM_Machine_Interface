import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * File: Persistence.java
 * Responsibility: Provides file-based persistence for accounts using java.io.
 * Loads and rewrites account records to accounts.txt (CSV format: userId,pin,balance).
 */
public class Persistence {
    private static final String FILE_NAME = "accounts.txt";

    /**
     * Loads accounts from accounts.txt.
     * If the file does not exist or is empty, creates it with default mock accounts.
     *
     * @return ArrayList of loaded Account instances
     */
    public static ArrayList<Account> loadAccounts() {
        ArrayList<Account> accounts = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            // Seed default mock accounts on first run
            accounts.add(new Account("user1", "1234", 1000.00));
            accounts.add(new Account("user2", "5678", 500.00));
            accounts.add(new Account("user3", "9999", 2500.00));
            saveAccounts(accounts);
            return accounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String userId = parts[0].trim();
                    String pin = parts[1].trim();
                    double balance = Double.parseDouble(parts[2].trim());
                    accounts.add(new Account(userId, pin, balance));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Warning: Error reading accounts file: " + e.getMessage());
        }

        // Fallback if file was empty
        if (accounts.isEmpty()) {
            accounts.add(new Account("user1", "1234", 1000.00));
            accounts.add(new Account("user2", "5678", 500.00));
            accounts.add(new Account("user3", "9999", 2500.00));
            saveAccounts(accounts);
        }

        return accounts;
    }

    /**
     * Resets accounts.txt back to initial default seed values.
     * Useful for automated test isolation and database resetting.
     */
    public static void resetDefaults() {
        ArrayList<Account> defaults = new ArrayList<>();
        defaults.add(new Account("user1", "1234", 1000.00));
        defaults.add(new Account("user2", "5678", 500.00));
        defaults.add(new Account("user3", "9999", 2500.00));
        saveAccounts(defaults);
    }

    /**
     * Rewrites the accounts.txt file with the current state of all accounts.
     *
     * @param accounts The accounts list to persist
     */
    public static void saveAccounts(ArrayList<Account> accounts) {
        if (accounts == null) {
            return;
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(FILE_NAME)))) {
            for (Account account : accounts) {
                writer.printf("%s,%s,%.2f%n", account.getUserId(), account.getPin(), account.getBalance());
            }
        } catch (IOException e) {
            System.err.println("Warning: Error saving accounts file: " + e.getMessage());
        }
    }
}
