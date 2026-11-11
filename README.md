# Console-Based ATM Machine Simulation

A robust, object-oriented console simulation of an Automated Teller Machine (ATM) developed in pure Java SE. The system simulates real-world banking operations including PIN authentication with a 3-attempt lockout policy, cash withdrawals with balance pre-validation, cash deposits, atomic inter-account funds transfers, session-isolated transaction history, and persistent file-based storage.

---

## Tech Stack
* **Language:** Java (JDK 8 or higher; tested across JDK 8, 11, 17, 21, and 25)
* **Application Type:** Console / Command-Line Interface (CLI)
* **Architecture:** Object-Oriented Design (Clean Architecture, Encapsulation, SoC, SRP)
* **Persistence:** Standard File I/O (`java.io.BufferedReader`, `java.io.PrintWriter`)
* **Libraries:** Pure Java SE standard library only (no external dependencies, frameworks, or databases)

---

## How to Compile and Run

### Prerequisites
* JDK 8 or higher installed and accessible via system `PATH`.

### Commands
1. **Navigate to the project root directory:**
   ```bash
   cd e:\Projects\ATM_Machine_Interface
   ```

2. **Compile all Java source files:**
   ```bash
   javac *.java
   ```

3. **Run the application:**
   ```bash
   java Main
   ```

4. **(Optional) Run the automated unit test suite:**
   ```bash
   java TestATM
   ```

---

## Mock User Credentials

The application automatically seeds three mock customer accounts upon initial launch (stored in `accounts.txt`):

| User ID | Secret PIN | Initial Balance | Description / Role |
| :--- | :--- | :--- | :--- |
| **`user1`** | **`1234`** | **$1,000.00** | Primary test account (withdrawals, deposits, transfers) |
| **`user2`** | **`5678`** | **$500.00** | Secondary account / transfer recipient |
| **`user3`** | **`9999`** | **$2,500.00** | High-balance alternate account |

*Note: User IDs are case-insensitive (e.g., `user1` or `USER1`), while PINs require an exact string match.*

---

## Feature Checklist

- [x] Startup prompt for User ID and PIN authentication
- [x] Security lockout after 3 consecutive incorrect PIN attempts (terminates program)
- [x] Interactive main menu driven by a `switch-case` loop:
  - [x] Option 1: Transaction History — Displays formatted session-only audit table
  - [x] Option 2: Withdraw — Prompts for amount, verifies balance, debits account, logs action
  - [x] Option 3: Deposit — Prompts for amount, credits account, logs action
  - [x] Option 4: Transfer — Prompts for recipient, guards against self-transfers, atomically updates both accounts, logs action
  - [x] Option 5: Quit — Displays farewell message and exits cleanly
- [x] Strict prerequisite balance check BEFORE any withdrawal or transfer
- [x] Exact case-sensitive phrase `"Insufficient Funds"` output upon overdraw attempts
- [x] All transactions stored in an `ArrayList<Transaction>`
- [x] Transaction history is per-session (cleared on every successful login)
- [x] Robust input hardening: non-numeric inputs, negative/zero amounts, and blank lines are rejected without crashing
- [x] Pure Object-Oriented design across at least 5 distinct Java classes (`Main`, `Account`, `Transaction`, `Bank`, `ATM`)
- [x] File persistence using `accounts.txt` updated after every debit, credit, and transfer

---

## Class Diagram

```text
+---------------------------------------------------------------------------------+
|                                     Main                                        |
|  - Creates Bank instance                                                        |
|  - Injects Bank into ATM                                                        |
|  - Calls atm.start()                                                            |
+---------------------------------------------------------------------------------+
                                       |
                                       | instantiates & invokes
                                       v
+---------------------------------------------------------------------------------+
|                                     ATM                                         |
|  - Bank bank                                                                    |
|  - Scanner scanner                                                              |
|  - Account currentAccount                                                       |
|  - ArrayList<Transaction> currentSessionHistory  *--------+                     |
+-----------------------------------------------------------|---------------------+
                                       |                    |
                                       | queries & transfers| creates & appends
                                       v                    v
+-------------------------------------------------+   +---------------------------+
|                      Bank                       |   |        Transaction        |
|  - ArrayList<Account> accounts                  |   |  - String type            |
+-------------------------------------------------+   |  - double amount          |
                        |                             |  - LocalDateTime timestamp|
                        | aggregates & manages        |  - String details         |
                        v                             +---------------------------+
+-------------------------------------------------+
|                     Account                     |
|  - String userId                                |
|  - String pin                                   |
|  - double balance                               |
+-------------------------------------------------+
```

---

## Known Limitations
1. **In-Memory Currency Representation:** Currency values use `double` rather than `java.math.BigDecimal`, which is acceptable for educational simulations but susceptible to floating-point rounding errors in high-volume production banking.
2. **Plaintext Credential Storage:** PINs are stored in plaintext within `accounts.txt` without cryptographic hashing (e.g., bcrypt/Argon2).
3. **Single-Threaded Execution:** Account transfers and disk operations are not thread-safe; concurrent access by multiple threads would require synchronization or transactional locking.
4. **Session-Only Audit Logs:** Transaction logs are scoped to the current user session and discarded upon logout rather than persisted long-term to an audit log file.

---

## Suggested Extensions
1. **Cryptographic PIN Hashing:** Hash PINs using SHA-256 or bcrypt with unique salts prior to saving to `accounts.txt`.
2. **Persistent Transaction Statement Export:** Allow users to export their transaction history to a persistent CSV or PDF bank statement file (`statement_<userId>.txt`).
3. **Arbitrary-Precision Accounting:** Migrate all currency fields and calculations from `double` to `java.math.BigDecimal` with `RoundingMode.HALF_EVEN`.
4. **Banknote Dispenser Breakdown:** Simulate physical ATM hardware dispensing optimal banknote denominations ($100, $50, $20, $10 bills) with cash vault reserve limits.
5. **Administrative Console:** Provide an admin maintenance login to view total bank vault reserves, inspect all accounts, unfreeze locked profiles, and restock cash.
