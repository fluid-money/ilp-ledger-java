package money.fluid.ilp.ledger.exceptions;

import org.interledger.cryptoconditions.Fulfillment;

/**
 * Thrown when a transferFunds's {@link Fulfillment} is not found.
 */
public class TransferFulfillmentNotFoundException extends InterledgerException {
    /**
     * Creates a new instance of {@code InsufficientAmountException}
     * without detail message.
     */
    public TransferFulfillmentNotFoundException() {
    }

    /**
     * Constructs an instance of {@code InsufficientAmountException} with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public TransferFulfillmentNotFoundException(String msg) {
        super(msg);
    }
}
