package money.fluid.ilp.ledger.model;

import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import javax.money.MonetaryAmount;
import java.time.ZonedDateTime;
import java.util.Optional;

@Immutable
public abstract class EscrowInputs {

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
     * Return a {@link MonetaryAmount} representing the amount of funds to escrow.
     */
    @Parameter
    public abstract MonetaryAmount amount();

    /**
     * The date and time that this escrow expires.
     */
    @Parameter
    public abstract Optional<ZonedDateTime> expirationDateTime();

}
