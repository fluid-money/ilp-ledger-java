package money.fluid.ilp.ledger.exceptions;

import money.fluid.ilp.ledger.model.Transfer;
import org.interledger.cryptoconditions.Condition;


/**
 * Thrown when a {@link Transfer} The transferFunds had no {@link Condition} to fulfill.
 */
public class TransferNotConditionalException extends InterledgerException {
    /**
     * Creates a new instance of {@code InsufficientAmountException}
     * without detail message.
     */
    public TransferNotConditionalException() {
    }

    /**
     * Constructs an instance of {@code InsufficientAmountException} with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public TransferNotConditionalException(String msg) {
        super(msg);
    }
}
