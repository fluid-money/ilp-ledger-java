package money.fluid.ilp.ledger.exceptions;

import money.fluid.ilp.ledger.model.Transfer;
import org.interledger.cryptoconditions.Fulfillment;

/**
 * Thrown when a {@link Fulfillment} does not match the corresponding {@link Transfer#getExecutionCondition()}.
 */
public class UnmetConditionException extends InterledgerException {
    /**
     * Creates a new instance of {@code InsufficientAmountException}
     * without detail message.
     */
    public UnmetConditionException() {
    }

    /**
     * Constructs an instance of {@code InsufficientAmountException} with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnmetConditionException(String msg) {
        super(msg);
    }
}
