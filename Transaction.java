import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Models an immutable financial audit log entry representing a completed transaction.
 * Captures the transaction type (WITHDRAW, DEPOSIT, TRANSFER), the monetary amount,
 * the creation timestamp, and descriptive context notes.
 *
 * @author Senior Java Architect
 * @version 1.0
 */
public class Transaction {
    private final String type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String details;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructs a new immutable Transaction log entry with an automated system timestamp.
     *
     * @param type    The category of financial transaction (e.g., "WITHDRAW", "DEPOSIT", "TRANSFER").
     * @param amount  The monetary value involved in the transaction.
     * @param details Additional context, descriptive notes, or updated balance summaries.
     */
    public Transaction(String type, double amount, String details) {
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    /**
     * Retrieves the financial transaction category type.
     *
     * @return The transaction type string.
     */
    public String getType() {
        return type;
    }

    /**
     * Retrieves the monetary amount associated with this transaction.
     *
     * @return The transaction amount as a double.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Retrieves the exact date and time at which this transaction was recorded.
     *
     * @return A LocalDateTime timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Retrieves additional description and contextual details regarding the transaction.
     *
     * @return A descriptive detail string.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Formats the transaction into a standardized, tabular row for console display.
     *
     * @return An aligned string containing timestamp, type, amount, and details.
     */
    @Override
    public String toString() {
        return String.format("[%s] %-8s | Amount: $%9.2f | %s",
                timestamp.format(FORMATTER),
                type,
                amount,
                details);
    }
}
