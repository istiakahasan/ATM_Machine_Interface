# 🏧 ATM Machine Interface

A Java-based ATM Machine Simulation that demonstrates core banking operations using Object-Oriented Programming (OOP) principles. The application simulates essential ATM functionalities, including secure PIN authentication, balance management, deposits, withdrawals, fund transfers, and transaction history.

---

## 📌 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [System Architecture](#-system-architecture)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [Sample Login Credentials](#-sample-login-credentials)
- [Application Workflow](#-application-workflow)
- [Security & Validation](#-security--validation)
- [Known Limitations](#-known-limitations)
- [Future Improvements](#-future-improvements)
- [Author](#-author)
- [License](#-license)

---

## 🎯 Overview
The **ATM Machine Interface** is an educational banking simulation designed to demonstrate how basic ATM operations work in a software environment.

It follows object-oriented design principles to separate responsibilities between account management, banking operations, transaction processing, and user interaction.

The project focuses on:
- **Object-Oriented Programming (OOP)**
- **Encapsulation and separation of concerns**
- **Input validation and error handling**
- **Banking transaction management**
- **File-based data persistence**
- **Authentication and access control**

> **Note:** This project is an educational simulation and is not intended for real-world financial transactions.

---

## ✨ Features

### 🔐 1. User Authentication
- Login using a unique User ID and PIN.
- Case-insensitive User ID validation.
- Maximum of three consecutive failed login attempts.
- Automatic access denial after repeated authentication failures.

### 💰 2. Account Management
- Retrieve account information.
- Check available account balance.
- Maintain individual customer accounts.
- Protect account data through encapsulation.

### 💸 3. Cash Withdrawal
- Withdraw money from an authenticated account.
- Validate available balance before processing.
- Prevent zero or negative withdrawal amounts.
- Reject transactions when funds are insufficient.

### 💵 4. Cash Deposit
- Deposit money into an account.
- Validate transaction amounts.
- Automatically update the account balance.

### 🔄 5. Fund Transfer
- Transfer funds between customer accounts.
- Validate recipient information.
- Prevent transfers to the same account.
- Check available balance before processing.
- Update both source and recipient balances.

### 📜 6. Transaction History
- Maintain transaction records during the current login session.
- Display transaction types, amounts, timestamps, and details.
- Support withdrawal, deposit, and transfer records.

### 💾 7. File-Based Data Persistence
- Store account information in `accounts.txt`.
- Load account records when the application starts.
- Update stored account balances after financial operations.

### 🛡️ 8. Input Validation & Error Handling
- Handle invalid numeric input.
- Reject negative and zero transaction amounts.
- Prevent insufficient-balance transactions.
- Handle invalid account IDs.
- Avoid application crashes caused by unexpected user input.

---

## 🛠️ Technology Stack

| Technology | Purpose |
| :--- | :--- |
| **Java** | Core programming language |
| **Java OOP** | Application design and structure |
| **Java Collections** | Account and transaction management |
| **ArrayList** | Transaction history |
| **Java File I/O** | File-based persistence |
| **Maven** | Project build and dependency management |
| **JUnit / Java Testing** | Testing support |

---

## 🏗️ System Architecture
The application follows a modular, object-oriented architecture.

### Core Components

| Class | Responsibility |
| :--- | :--- |
| `Main.java` | Application entry point and initialization |
| `ATM.java` | User interaction, authentication, and ATM operations |
| `Account.java` | Customer account information and balance management |
| `Bank.java` | Account management and fund transfer coordination |
| `Transaction.java` | Transaction records and details |
| `Persistence.java` | Account data storage and retrieval |

### Application Flow

```text
+------------------+
|       Main       |
+------------------+
         |
         v
+------------------+
|       ATM        |
+------------------+
         |
    +----+----+
    |         |
    v         v
+-------+ +-------------+
| Bank  | | Transaction |
+-------+ +-------------+
    |
    v
+---------+
| Account |
+---------+
    |
    v
+-------------+
| Persistence |
+-------------+
    |
    v
accounts.txt
```

For a more detailed explanation of the design, class responsibilities, and transaction workflows, see [ARCHITECTURE.md](ARCHITECTURE.md).

---

## 📂 Project Structure

```text
ATM_Machine_Interface/
│
├── src/
│
├── .mvn/
│   └── wrapper/
│
├── Main.java
├── ATM.java
├── Account.java
├── Bank.java
├── Transaction.java
├── Persistence.java
│
├── TestATM.java
├── accounts.txt
│
├── pom.xml
├── mvnw
├── mvnw.cmd
│
├── ARCHITECTURE.md
├── README.md
└── .gitignore
```

---

## 🚀 Getting Started

### Prerequisites
Make sure you have the following installed:
- Java Development Kit (JDK) 8 or higher.
- Git.
- IntelliJ IDEA, Eclipse, or VS Code.
- Maven (if building through Maven).

### 1. Clone the Repository
```bash
git clone https://github.com/istiakahasan/ATM_Machine_Interface.git
```

### 2. Navigate to the Project
```bash
cd ATM_Machine_Interface
```

### 3. Open the Project
Open the cloned repository in your preferred Java IDE.

### 4. Build the Project
If the Maven project configuration is being used:

**Windows:**
```powershell
.\mvnw.cmd clean package
```

**Linux / macOS:**
```bash
./mvnw clean package
```

### 5. Run the Application
Run the appropriate application entry point from your IDE or terminal after confirming the source layout and configured main class:
```bash
javac *.java
java Main
```

---

## 🔑 Sample Login Credentials
The simulation provides three predefined test accounts:

| User ID | PIN | Initial Balance |
| :--- | :--- | :--- |
| `user1` | `1234` | $1,000.00 |
| `user2` | `5678` | $500.00 |
| `user3` | `9999` | $2,500.00 |

> **Important:** These are demonstration credentials only. Do not use real banking credentials.

---

## 🔄 Application Workflow
The general application workflow is:
1. Launch the ATM application.
2. Enter the User ID and PIN.
3. Complete authentication.
4. Access the main ATM menu.
5. Select the required banking operation.
6. Enter the transaction details.
7. Validate the requested operation.
8. Update the account information.
9. Save account changes.
10. View transaction results or transaction history.
11. Exit the application.

---

## 🛡️ Security & Validation
The application demonstrates several basic security and validation mechanisms:
- Limited authentication attempts.
- Encapsulated account information.
- Balance validation before withdrawals.
- Input validation for financial operations.
- Prevention of self-transfers.
- Validation of recipient accounts.
- Separation of presentation and business logic.

*These are educational security measures and should not be considered equivalent to production-grade banking security.*

---

## ⚠️ Known Limitations
The current implementation has some limitations:
- **PIN Storage:** Demonstration credentials are stored in plaintext.
- **Currency Precision:** Monetary calculations use `double` rather than `BigDecimal`.
- **Transaction History:** Transaction history is maintained per session.
- **Concurrency:** Concurrent banking operations are not designed for multi-user production workloads.
- **Persistence:** File-based storage does not provide database transaction guarantees.
- **Production Security:** The application does not implement the complete security controls required by a real banking platform.

---

## 🔮 Future Improvements
Potential enhancements include:
- Replace file-based storage with PostgreSQL or MySQL.
- Implement secure PIN hashing.
- Introduce Spring Security authentication.
- Use `BigDecimal` for financial calculations.
- Develop RESTful banking APIs.
- Implement database-backed transaction history.
- Add role-based access control.
- Introduce automated unit and integration testing.
- Develop a graphical web-based ATM interface.
- Add an administrative dashboard.

---

## 👨‍💻 Author
**Istiak Ahasan**
- GitHub: [@istiakahasan](https://github.com/istiakahasan)
- LinkedIn: [Istiak Ahsan](https://linkedin.com/in/istiak-ahsan/)

---

## 📄 License
This project is intended for educational and learning purposes.  
If you plan to distribute or reuse this project, consider adding an appropriate open-source license.

---
⭐ **If you find this project useful, consider giving the repository a star!**
