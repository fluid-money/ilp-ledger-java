package money.fluid.ilp.ledger.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;

import javax.money.MonetaryAmount;
import java.util.Objects;
import java.util.UUID;

@ToString
@EqualsAndHashCode
public class LedgerAccount {

    // This value is used to ensure inequality between two different instances of an Account.  Primarily this is useful
    // when replacing accounts in a ConcurrentHashmap where the functionality relies on an equality check between "old"
    // "new" instances.
    @NonNull
    private final UUID snapshotIdentifier = UUID.randomUUID();

    @NonNull
    @Getter
    private final LedgerAccountId ledgerAccountId;

    @NonNull
    @Getter
    private final MonetaryAmount balance;

    /**
     * Required-args Constructor.
     *
     * @param ledgerAccountId
     * @param balance
     */
    public LedgerAccount(
            final LedgerAccountId ledgerAccountId, final MonetaryAmount balance
    ) {
        this.ledgerAccountId = Objects.requireNonNull(ledgerAccountId);
        this.balance = Objects.requireNonNull(balance);
    }

    /**
     * Create a new instance of {@link LedgerAccount} with a new balance.
     *
     * @param monetaryAmount
     * @return
     */
    public LedgerAccount withNewBalance(final MonetaryAmount monetaryAmount) {
        return new LedgerAccount(this.getLedgerAccountId(), monetaryAmount);
    }

}
