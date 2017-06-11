package money.fluid.ilp.ledger.services;

import money.fluid.ilp.ledger.exceptions.EscrowException;
import money.fluid.ilp.ledger.model.EscrowAccount;
import money.fluid.ilp.ledger.model.EscrowInputs;
import money.fluid.ilp.ledger.model.ids.EscrowAccountId;

import java.util.Optional;

/**
 * A service that manages escrow for a Ledger.  A ledger will hold some assets in escrow that, when executed, will be
 * credited to the target account for an escrow.  Conversely, if an escrow is reversed, then the assets will be credited
 * back to the source account for that escrow.
 */
public interface EscrowService {
    /**
     * Create an escrow account and hold funds in it by transfering funds out of the inidcated source account, in
     * preparation of executing or reversing the escrow.
     *
     * @param escrowInputs An instance of {@link EscrowInputs} with all information required to create an escrow
     *                     acount.
     * @return
     */
    EscrowAccount initiateEscrow(final EscrowInputs escrowInputs) throws EscrowException;

    /**
     * Get an instance of {@link EscrowAccount} for the specified {@link EscrowAccountId}, if it exists.
     *
     * @param escrowAccountId
     * @return An instance of {@link EscrowAccount}, or {@link Optional#empty()} if no such account exists.
     */
    Optional<EscrowAccount> getEscrowAccount(final EscrowAccountId escrowAccountId);

    /**
     * For a given {@link EscrowAccount} with a status of {@link EscrowAccount.Status#PENDING}, execute the escrow by
     * transferring funds from {@link EscrowAccount#sourceAccountId()} to the {@link
     * EscrowAccount#destinationAccountId()}.
     *
     * @param escrowAccountId An instance of {@link EscrowAccountId} that identifies the pending escrow account to
     *                        execute.
     * @return
     * @throws EscrowException if the escrow execution failed for any reason.
     */
    EscrowAccount executeEscrow(final EscrowAccountId escrowAccountId) throws EscrowException;

    /**
     * For a given {@link EscrowAccount} with a status of {@link EscrowAccount.Status#PENDING}, reverse the escrow by
     * transferring funds from {@link EscrowAccount#destinationAccountId()} to the {@link
     * EscrowAccount#sourceAccountId()}.
     *
     * @param escrowAccountId An instance of {@link EscrowAccountId} that identifies the pending escrow account to
     *                        execute.
     * @return
     * @throws EscrowException if the escrow execution failed for any reason.
     */
    EscrowAccount reverseEscrow(final EscrowAccountId escrowAccountId) throws EscrowException;
}
