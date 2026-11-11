package com.Banking.ATM_Machine_Interface;

/**
 * Application bootstrapper and composition root for the Console ATM Machine simulation.
 * Instantiates the Bank repository, connects the ATM terminal, and triggers the program loop.
 *
 * @author Senior Java Architect
 * @version 1.0
 */
public class Main {
    /**
     * Program execution entry point.
     *
     * @param args Command-line arguments supplied at program launch (not utilized).
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

