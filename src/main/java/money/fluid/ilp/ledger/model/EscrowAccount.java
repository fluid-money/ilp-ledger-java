package money.fluid.ilp.ledger.model;

import money.fluid.ilp.ledger.model.ids.EscrowAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import javax.money.MonetaryAmount;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Models an escrow account, which only exists _after_ funds have been escrowed.
 */
@Immutable
public abstract class EscrowAccount {

    /**
     * Return the {@link EscrowAccountId} that uniquely identifies this escrow.
     */
    @Parameter
    public abstract EscrowAccountId escrowId();

    /**
     * Return the {@link LedgerAccountId} of this escrow's source of funds.
     */
    @Parameter
    public abstract LedgerAccountId sourceAccountId();

    /**
     * Return the {@link LedgerAccountId} of this escrow's source of funds.
     */
    @Parameter
    public abstract LedgerAccountId destinationAccountId();

    /**
     * Return a {@link MonetaryAmount} representing the amount of funds in this escrow account.
     */
    @Parameter
    public abstract MonetaryAmount amount();

    /**
     * The date and time that this escrow expires.
     */
    @Parameter
    public abstract Optional<ZonedDateTime> expirationDateTime();

    /**
     * The {@link Status} of this escrow.
     */
    @Parameter
    public abstract EscrowAccount.Status status();

    public EscrowAccount withNewStatus(final Status newStatus) {
        return ImmutableEscrowAccount.copyOf(this).withStatus(newStatus);
    }

    public enum Status {
        // The Escrow has is waiting to be fulfilled or rejected.
        PENDING,
        // The escrow has been executed.  See Javadoc on EscrowService#execute.
        EXECUTED,
        // The escrow has been reversed.  See Javadoc on EscrowService#reverse
        REVERSED
    }
}
