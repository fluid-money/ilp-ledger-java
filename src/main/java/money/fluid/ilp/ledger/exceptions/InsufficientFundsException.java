package money.fluid.ilp.ledger.exceptions;


import money.fluid.ilp.ledger.model.Transfer;

/**
 * Thrown when {@link Transfer#getDebitAccountId()} would go below its minimum allowed balance if this transferFunds were
 * executed.
 */
public class InsufficientFundsException extends InterledgerException {
    /**
     * Creates a new instance of {@code InsufficientAmountException}
     * without detail message.
     */
    public InsufficientFundsException() {
    }

    /**
     * Constructs an instance of {@code InsufficientAmountException} with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}
