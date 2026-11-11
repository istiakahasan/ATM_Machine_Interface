import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * File: TestATM.java
 * Responsibility: Automated test runner for the ATM system using pure Java SE (no external frameworks).
 * Executes 6 distinct test assertions covering authentication, lockout, balance checks,
 * deposits, transfers, and transaction history tracking.
 */
public class TestATM {
    private static int passedCount = 0;
    private static int failedCount = 0;

    // Helper holder for ATM reference and captured console output
    private static class ExecutionResult {
        final ATM atm;
        final String output;

        ExecutionResult(ATM atm, String output) {
            this.atm = atm;
            this.output = output;
        }
    }

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                     RUNNING ATM AUTOMATED TEST SUITE                           ");
        System.out.println("================================================================================");

        test1_CorrectPinLogsIn();
        test2_WrongPinThreeTimesExits();
        test3_WithdrawExceedingBalancePrintsInsufficientFunds();
        test4_DepositIncreasesBalance();
        test5_TransferMovesMoneyFromAToB();
        test6_HistorySizeMatchesOperations();

        System.out.println("================================================================================");
        System.out.printf("TEST RESULTS: %d PASSED, %d FAILED (TOTAL %d)%n",
                passedCount, failedCount, (passedCount + failedCount));
        System.out.println("================================================================================");

        if (failedCount > 0) {
            System.exit(1);
        }
    }

    /**
     * Assertion 1: Correct PIN logs in successfully.
     */
    private static void test1_CorrectPinLogsIn() {
        Bank bank = new Bank();
        // Input: user1 / PIN 1234 / Menu 5 (Quit)
        String simulatedInput = "user1\n1234\n5\n";
        ExecutionResult result = executeAtm(simulatedInput, bank);

        boolean condition = result.output.contains("Authentication Successful! Welcome, user1.")
                && result.atm.getCurrentAccount() != null
                && "user1".equalsIgnoreCase(result.atm.getCurrentAccount().getUserId());

        recordAssertion("1. Correct PIN logs in", condition,
                condition ? "Authenticated successfully and initialized session."
                          : "Failed to authenticate valid user credentials.");
    }

    /**
     * Assertion 2: Entering wrong PIN 3 times triggers lockout and terminates.
     */
    private static void test2_WrongPinThreeTimesExits() {
        Bank bank = new Bank();
        // Input: 3 consecutive wrong PIN entries
        String simulatedInput = "user1\n0000\nuser1\n1111\nuser1\n2222\n";
        ExecutionResult result = executeAtm(simulatedInput, bank);

        boolean condition = result.output.contains("[ACCESS DENIED] 3 incorrect attempts. Access locked. Exiting program...")
                && result.atm.getCurrentAccount() == null;

        recordAssertion("2. Wrong PIN 3 times exits", condition,
                condition ? "Application terminated and locked after 3 failed attempts."
                          : "Did not properly enforce 3-attempt lockout.");
    }

    /**
     * Assertion 3: Withdrawing an amount exceeding balance prints exact "Insufficient Funds".
     */
    private static void test3_WithdrawExceedingBalancePrintsInsufficientFunds() {
        Bank bank = new Bank();
        // Input: user1 ($1,000 balance) -> Option 2 (Withdraw) -> $5,000.00 -> Option 5 (Quit)
        String simulatedInput = "user1\n1234\n2\n5000\n5\n";
        ExecutionResult result = executeAtm(simulatedInput, bank);

        boolean printedMessage = result.output.contains("Insufficient Funds");
        boolean balanceUnchanged = (result.atm.getCurrentAccount() != null
                && Math.abs(result.atm.getCurrentAccount().getBalance() - 1000.00) < 0.001);
        boolean condition = printedMessage && balanceUnchanged;

        recordAssertion("3. Withdraw > balance prints \"Insufficient Funds\"", condition,
                condition ? "Printed exact phrase 'Insufficient Funds' without balance mutation."
                          : "Failed: 'Insufficient Funds' was not printed or balance was modified.");
    }

    /**
     * Assertion 4: Deposit increases account balance accordingly.
     */
    private static void test4_DepositIncreasesBalance() {
        Bank bank = new Bank();
        // Input: user1 ($1,000 balance) -> Option 3 (Deposit) -> $250.00 -> Option 5 (Quit)
        String simulatedInput = "user1\n1234\n3\n250\n5\n";
        ExecutionResult result = executeAtm(simulatedInput, bank);

        double balance = result.atm.getCurrentAccount().getBalance();
        boolean condition = Math.abs(balance - 1250.00) < 0.001;

        recordAssertion("4. Deposit increases balance", condition,
                condition ? String.format("Balance correctly increased to $%.2f.", balance)
                          : String.format("Expected $1250.00 but balance was $%.2f.", balance));
    }

    /**
     * Assertion 5: Transfer moves money from sender account A to recipient account B.
     */
    private static void test5_TransferMovesMoneyFromAToB() {
        Bank bank = new Bank();
        Account sender = bank.findAccount("user1");    // Initial: $1,000.00
        Account recipient = bank.findAccount("user2"); // Initial: $500.00

        // Input: user1 -> Option 4 (Transfer) -> recipient: user2 -> amount: $300.00 -> Option 5 (Quit)
        String simulatedInput = "user1\n1234\n4\nuser2\n300\n5\n";
        executeAtm(simulatedInput, bank);

        boolean senderCorrect = Math.abs(sender.getBalance() - 700.00) < 0.001;
        boolean recipientCorrect = Math.abs(recipient.getBalance() - 800.00) < 0.001;
        boolean condition = senderCorrect && recipientCorrect;

        recordAssertion("5. Transfer moves money from A to B", condition,
                condition ? String.format("Sender balance: $%.2f, Recipient balance: $%.2f.", sender.getBalance(), recipient.getBalance())
                          : String.format("Balances inaccurate. Sender: $%.2f, Recipient: $%.2f.", sender.getBalance(), recipient.getBalance()));
    }

    /**
     * Assertion 6: History size matches total number of operations performed in the session.
     */
    private static void test6_HistorySizeMatchesOperations() {
        Bank bank = new Bank();
        // Input: Perform 3 operations: Deposit $200, Withdraw $50, Transfer $100 to user2
        String simulatedInput = "user1\n1234\n3\n200\n2\n50\n4\nuser2\n100\n5\n";
        ExecutionResult result = executeAtm(simulatedInput, bank);

        int recordedSize = result.atm.getCurrentSessionHistory().size();
        boolean condition = (recordedSize == 3);

        recordAssertion("6. History size matches number of operations", condition,
                condition ? String.format("Session log recorded exactly %d transactions.", recordedSize)
                          : String.format("Expected 3 transactions, but recorded %d.", recordedSize));
    }

    /**
     * Helper to execute the ATM with redirected System.in and captured System.out.
     * Crucially initializes ATM AFTER System.setIn so the Scanner binds to simulated input.
     * Restores original input and output streams once finished.
     */
    private static ExecutionResult executeAtm(String simulatedInput, Bank bank) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
            System.setOut(new PrintStream(capturedOutput));

            ATM atm = new ATM(bank);
            atm.start();

            return new ExecutionResult(atm, capturedOutput.toString());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    /**
     * Logs assertion status formatted as [PASS] or [FAIL].
     */
    private static void recordAssertion(String label, boolean passed, String message) {
        if (passed) {
            passedCount++;
            System.out.printf("[PASS] %-52s | %s%n", label, message);
        } else {
            failedCount++;
            System.out.printf("[FAIL] %-52s | %s%n", label, message);
        }
    }
}
