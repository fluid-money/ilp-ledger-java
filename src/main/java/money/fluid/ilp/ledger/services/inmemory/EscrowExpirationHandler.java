package money.fluid.ilp.ledger.services.inmemory;

import money.fluid.ilp.ledger.model.EscrowAccount;

/**
 * An internal-use-only interface that connects Guava cache timeouts to this EscrowManager.  Only really valid for this
 * implementation.
 * <p>
 * NOTE: This interface is purposefully not part of EscrowManager because this interface only exists for the in-memory
 * Ledger impl.
 */
interface EscrowExpirationHandler {
    /**
     * Called when an instance of {@link EscrowAccount} has timed-out and is no longer valid.
     *
     * @param expiredEscrowAccount
     */
    void onEscrowTimedOut(final EscrowAccount expiredEscrowAccount);
}
