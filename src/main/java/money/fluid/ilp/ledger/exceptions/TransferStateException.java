package money.fluid.ilp.ledger.exceptions;


import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Status;

/**
 * Thrown when {@link Transfer} in not in the {@link Status#CREATED} state when the request was received. This occurs
 * if the transferFunds has already been executed, rejected, or expired.
 */
public class TransferStateException extends InterledgerException {
    /**
     * Creates a new instance of {@code InsufficientAmountException}
     * without detail message.
     */
    public TransferStateException() {
    }

    /**
     * Constructs an instance of {@code InsufficientAmountException} with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public TransferStateException(String msg) {
        super(msg);
    }
}
