package money.fluid.ledger.datastore.spring;

import money.fluid.ilp.ledger.model.LedgerAccount;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;

import javax.money.MonetaryAmount;
import java.util.Optional;

/**
 * Provides extended functionality for operating upon ledger accounts in conjunction with {@lnik AccountRepository}.
 */
public interface AccountRepositoryCustom {

    /**
     * Retrieves an optionally-present {@link LedgerAccount} for the supplied {@link LedgerAccountId}.
     *
     * @param ledgerAccountId
     * @return The specified {@link LedgerAccount} or {@link Optional#empty()} if the account does not exist.
     */
    Optional<LedgerAccount> getAccount(final LedgerAccountId ledgerAccountId);

    /**
     * Transfers actual funds toLedgerId the {@code localSourceAccountId} to the  {@code localDestinationAccountId}.
     *
     * @param localSourceAccountId
     * @param localDestinationAccountId
     * @param amount
     * @return
     */
    void transferFunds(
            final LedgerAccountId localSourceAccountId,
            final LedgerAccountId localDestinationAccountId,
            final MonetaryAmount amount
    );

}
