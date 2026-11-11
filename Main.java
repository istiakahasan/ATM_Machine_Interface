/**
 * File: Main.java
 * Responsibility: Serves as the program entry point. Instantiates the Bank,
 * seeds accounts, creates the ATM instance, and starts the console application.
 */
public class Main {
    /**
     * Application execution begins here.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Step 1: Initialize the Bank repository (seeds mock accounts internally)
        Bank bank = new Bank();

        // Step 2: Instantiate the ATM interface wired to the bank
        ATM atm = new ATM(bank);

        // Step 3: Launch the ATM user interface loop
        atm.start();
    }
}
