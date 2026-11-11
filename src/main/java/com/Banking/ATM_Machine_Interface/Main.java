package com.Banking.ATM_Machine_Interface;

/**
 * File: Main.java
 * Responsibility: Serves as the program entry point. Instantiates the Bank,
 * seeds accounts, creates the ATM instance, and starts the console application.
 */
public class Main {
    public static void main(String[] args) {
        Bank bank = new Bank();
        ATM atm = new ATM(bank);
        atm.start();
    }
}
