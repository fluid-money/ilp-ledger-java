package money.fluid.ilp.ledger.exceptions;


import money.fluid.ilp.ledger.model.Transfer;

/**
 * Thrown when a {@link Transfer} contains an {@link Transfer#getExecutionCondition()} whose feature bitstring requires
 * functionality not implemented by this ledger.
 */
public class UnsupportedCryptoConditionException extends InterledgerException {
    /**
     * Creates a new instance of {@code InsufficientAmountException}
     * without detail message.
     */
    public UnsupportedCryptoConditionException() {
    }

    /**
     * Constructs an instance of {@code InsufficientAmountException} with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public UnsupportedCryptoConditionException(String msg) {
        super(msg);
    }
}
