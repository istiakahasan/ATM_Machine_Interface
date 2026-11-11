/**
 * File: TestATM.java
 * Responsibility: Plain Java test runner executing 8 unit assertions covering
 * Account credentials, debit/credit logic, transfer coordination, self-transfer blocking,
 * and Bank account lookup.
 */
public class TestATM {
    private static int passed = 0;
    private static final int TOTAL_TESTS = 8;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                         ATM SYSTEM UNIT TEST SUITE                             ");
        System.out.println("================================================================================");

        test1_CorrectPin();
        test2_WrongPin();
        test3_DebitReducesBalance();
        test4_DebitPastZero();
        test5_CreditIncreasesBalance();
        test6_TransferMovesMoney();
        test7_TransferToSelfBlocked();
        test8_FindNonexistentAccountReturnsNull();

        System.out.println("================================================================================");
        System.out.printf("%d/%d tests passed%n", passed, TOTAL_TESTS);
        System.out.println("================================================================================");

        if (passed != TOTAL_TESTS) {
            System.exit(1);
        }
    }

    /**
     * 1. Correct PIN returns true from validatePin
     */
    private static void test1_CorrectPin() {
        Account account = new Account("tester", "1234", 1000.00);
        boolean result = account.validatePin("1234");
        recordResult("1. Correct PIN returns true from validatePin", result);
    }

    /**
     * 2. Wrong PIN returns false
     */
    private static void test2_WrongPin() {
        Account account = new Account("tester", "1234", 1000.00);
        boolean result = !account.validatePin("9999");
        recordResult("2. Wrong PIN returns false", result);
    }

    /**
     * 3. debit() reduces balance correctly
     */
    private static void test3_DebitReducesBalance() {
        Account account = new Account("tester", "1234", 1000.00);
        boolean debited = account.debit(300.00);
        boolean balanceCorrect = Math.abs(account.getBalance() - 700.00) < 0.001;
        recordResult("3. debit() reduces balance correctly", debited && balanceCorrect);
    }

    /**
     * 4. debit() past zero throws or returns false
     */
    private static void test4_DebitPastZero() {
        Account account = new Account("tester", "1234", 500.00);
        boolean debited = account.debit(900.00);
        boolean balanceUnchanged = Math.abs(account.getBalance() - 500.00) < 0.001;
        recordResult("4. debit() past zero throws or returns false", !debited && balanceUnchanged);
    }

    /**
     * 5. credit() increases balance correctly
     */
    private static void test5_CreditIncreasesBalance() {
        Account account = new Account("tester", "1234", 500.00);
        boolean credited = account.credit(250.00);
        boolean balanceCorrect = Math.abs(account.getBalance() - 750.00) < 0.001;
        recordResult("5. credit() increases balance correctly", credited && balanceCorrect);
    }

    /**
     * 6. transfer() moves money between accounts
     */
    private static void test6_TransferMovesMoney() {
        Account sender = new Account("sender", "1111", 1000.00);
        Account recipient = new Account("recipient", "2222", 500.00);
        Bank bank = new Bank();

        boolean transferred = bank.transfer(sender, recipient, 300.00);
        boolean senderCorrect = Math.abs(sender.getBalance() - 700.00) < 0.001;
        boolean recipientCorrect = Math.abs(recipient.getBalance() - 800.00) < 0.001;

        recordResult("6. transfer() moves money between accounts", transferred && senderCorrect && recipientCorrect);
    }

    /**
     * 7. transfer() to self is handled (blocked in our design)
     */
    private static void test7_TransferToSelfBlocked() {
        Account account = new Account("selfUser", "1234", 1000.00);
        Bank bank = new Bank();

        boolean allowed = bank.transfer(account, account, 100.00);
        boolean balanceUnchanged = Math.abs(account.getBalance() - 1000.00) < 0.001;

        recordResult("7. transfer() to self is handled", !allowed && balanceUnchanged);
    }

    /**
     * 8. findAccount("nonexistent") returns null
     */
    private static void test8_FindNonexistentAccountReturnsNull() {
        Bank bank = new Bank();
        Account result = bank.findAccount("nonexistent");
        recordResult("8. findAccount(\"nonexistent\") returns null", result == null);
    }

    /**
     * Formats output row as [PASS] or [FAIL].
     */
    private static void recordResult(String testName, boolean success) {
        if (success) {
            passed++;
            System.out.printf("[PASS] %s%n", testName);
        } else {
            System.out.printf("[FAIL] %s%n", testName);
        }
    }
}
