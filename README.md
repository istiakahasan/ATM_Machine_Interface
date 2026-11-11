# 🏧 Console-Based ATM Machine Simulation

[![Java](https://img.shields.io/badge/Java-8%2B%20%7C%2017%20%7C%2021%20%7C%2025-orange.svg)](https://www.oracle.com/java/)
[![Architecture](https://img.shields.io/badge/Architecture-Object--Oriented-blue.svg)](ARCHITECTURE.md)
[![Build](https://img.shields.io/badge/Build-Pure%20Java%20SE-brightgreen.svg)](#how-to-compile-and-run)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](#license)

A clean, robust, console-based simulation of an **Automated Teller Machine (ATM)** developed in pure **Java SE** adhering to modern Object-Oriented Programming (OOP) principles and clean architecture standards.

---

## 📌 Project Overview

This project simulates real-world ATM operations in a command-line environment. It enforces credential authentication with lockout protection, provides standard transactional banking workflows (withdrawals, deposits, inter-account transfers), checks balances prior to debits, and records session-specific transaction logs.

### Key Objectives
* **Object-Oriented Design**: Clean division of concerns across 5 core classes (`Main`, `Account`, `Bank`, `Transaction`, `ATM`).
* **Strict Encapsulation**: Private state protection; no direct external mutation of account balances.
* **Resilient Input Handling**: Graceful recovery from malformed, empty, or negative inputs without crashing.
* **Zero Dependencies**: Pure Java SE standard library — no external libraries, frameworks, or database setup required.

---

## ✨ Features

- [x] **Secure Authentication**: Prompt for User ID and PIN with a 3-attempt lockout policy.
- [x] **Interactive Console Menu**: Driven by a clean `switch-case` loop:
  - `1. Transaction History`: Real-time session-scoped transaction logs.
  - `2. Withdraw`: Cash deduction with prerequisite balance validation.
  - `3. Deposit`: Fund credit with instant balance update.
  - `4. Transfer`: Atomic funds transfer between two accounts.
  - `5. Quit`: Clean session termination with parting message.
- [x] **Sufficient Funds Guard**: Balance checked prior to withdrawal/transfer; displays exact `Insufficient Funds` alert if balance is insufficient.
- [x] **Session-Scoped History**: Every financial action records an immutable `Transaction` object into an `ArrayList<Transaction>` that resets per login for privacy.
- [x] **Input Validation**: Robust parsing preventing crashes on non-numeric characters, negative amounts, or self-transfers.

---

## 🏗️ Architecture Summary

```text
+-------------------------------------------------------------+
|                            Main                             |
|  - Creates Bank repository and injects into ATM             |
|  - Boots the ATM interface                                  |
+-------------------------------------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                             ATM                             |
|  - Presentation & console input controller (Scanner)        |
|  - Manages session lifecycle & current authenticated user   |
|  - Maintains currentSessionHistory (ArrayList<Transaction>) |
+-------------------------------------------------------------+
            |                                           |
            | delegates transfers & lookups             | creates & logs
            v                                           v
+-----------------------+                   +-------------------------+
|         Bank          |                   |       Transaction       |
|  - ArrayList<Account> |                   |  - Immutable log entry  |
|  - findAccount()      |                   |  - Timestamp, Type,     |
|  - transfer()         |                   |    Amount, Details      |
+-----------------------+                   +-------------------------+
            |
            | manages
            v
+---------------------------------------+
|                Account                |
|  - Private: userId, pin, balance      |
|  - Methods: validatePin(), debit(),   |
|    credit(), getBalance()             |
+---------------------------------------+
```

> 📖 **Deep Dive**: See [ARCHITECTURE.md](ARCHITECTURE.md) for full class diagrams, sequence flows, design patterns, and SOLID principle analysis.

---

## 🗂️ Project Structure

```
ATM_Machine_Interface/
├── Account.java        # Entity: Encapsulates user credentials and balance state
├── Transaction.java    # Value Object: Immutable log entry with timestamps
├── Bank.java           # Domain Service: Manages accounts and atomic transfers
├── ATM.java            # Presentation Controller: User interface, Scanner, and menu loop
├── Main.java           # Entry Point: Composition root bootstrapping the system
├── ARCHITECTURE.md     # Comprehensive architecture and design documentation
└── README.md           # Project documentation and user guide
```

---

## 🔑 Pre-Seeded Mock Accounts

The application automatically seeds 3 mock customer accounts for testing:

| User ID | PIN | Starting Balance | Purpose |
| :--- | :--- | :--- | :--- |
| **`user1`** | **`1234`** | **$1,000.00** | Primary test account |
| **`user2`** | **`5678`** | **$500.00** | Transfer recipient account |
| **`user3`** | **`9999`** | **$2,500.00** | High-balance alternate account |

*Note: User IDs are case-insensitive (`user1` or `USER1`), while PINs require an exact match.*

---

## 🚀 How to Compile and Run

### Prerequisites
* **Java Development Kit (JDK 8 or higher)**: Compatible with JDK 8, 11, 17, 21, and 25.
* Verify your Java installation:
  ```bash
  javac -version
  java -version
  ```

### Step 1: Open Terminal in Project Directory
Navigate to the root folder containing the `.java` files:
```bash
cd /path/to/ATM_Machine_Interface
```

### Step 2: Compile
Compile all 5 Java source files:
```bash
javac *.java
```

### Step 3: Run
Execute the compiled `Main` class:
```bash
java Main
```

---

## 🖥️ Walkthrough Demonstration

```text
==================================================
          WELCOME TO THE CONSOLE ATM              
==================================================

Enter User ID: user1
Enter PIN: 9999
Invalid User ID or PIN. Attempts remaining: 2

Enter User ID: user1
Enter PIN: 1234

Authentication Successful! Welcome, user1.

==================================================
                    MAIN MENU                     
==================================================
1. Transaction History
2. Withdraw
3. Deposit
4. Transfer
5. Quit
==================================================
Please enter your choice (1-5): 3

--- DEPOSIT CASH ---
Current Balance: $1000.00
Enter amount to deposit: $200
Deposit successful! Deposited: $200.00
Updated Balance: $1200.00

==================================================
Please enter your choice (1-5): 2

--- WITHDRAW CASH ---
Current Balance: $1200.00
Enter amount to withdraw: $50
Withdrawal successful! Dispensed: $50.00
Updated Balance: $1150.00

==================================================
Please enter your choice (1-5): 2

--- WITHDRAW CASH ---
Current Balance: $1150.00
Enter amount to withdraw: $5000
Insufficient Funds

==================================================
Please enter your choice (1-5): 4

--- TRANSFER FUNDS ---
Current Balance: $1150.00
Enter recipient User ID: user2
Enter amount to transfer: $100
Transfer successful! Sent $100.00 to user2.
Updated Balance: $1050.00

==================================================
Please enter your choice (1-5): 1

==================================================
          CURRENT SESSION TRANSACTION LOG         
==================================================
User ID: user1
Available Balance: $1050.00
--------------------------------------------------
1. [2026-11-11 20:08:03] DEPOSIT  | Amount: $   200.00 | Cash deposit. New balance: $1200.00
2. [2026-11-11 20:08:03] WITHDRAW | Amount: $    50.00 | Cash withdrawal. New balance: $1150.00
3. [2026-11-11 20:08:03] TRANSFER | Amount: $   100.00 | Transferred to user2. New balance: $1050.00
==================================================

==================================================
Please enter your choice (1-5): 5

Thank you for choosing our ATM. Goodbye!
```

---

## 🛡️ Edge Cases Handled

| Scenario | Input Condition | Expected System Behavior |
| :--- | :--- | :--- |
| **Failed Logins** | 3 consecutive incorrect PINs | Access denied message displayed; application terminates. |
| **Invalid Numeric Input** | Non-numeric strings (e.g. `abc`, `$100`) | Gracefully informs user and re-prompts without crashing. |
| **Non-Positive Amounts** | `$0` or negative values (e.g. `-$50`) | Rejects input; prompts for a positive amount. |
| **Low Balance** | Withdrawal/Transfer exceeding balance | Aborts transaction and outputs `Insufficient Funds`. |
| **Self-Transfer** | Transfer recipient same as logged-in user | Blocks operation with warning message. |
| **Invalid Recipient** | Non-existent user ID | Informs user recipient was not found; returns to menu. |
| **Blank Lines** | Empty enter key press | Prompt repeats safely without `IndexOutOfBoundsException`. |

---

## 📄 License

This project is licensed under the [MIT License](LICENSE). You are free to modify, distribute, and use it for learning or portfolio purposes.

