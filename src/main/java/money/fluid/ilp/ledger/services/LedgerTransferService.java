package money.fluid.ilp.ledger.services;

import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;

import java.util.Optional;

/**
 * A service for handling transferFunds requests initiated by the web-tier.
 */
public interface LedgerTransferService {

    /**
     * Create a new {@link Transfer} in this ledger by saving it to the attached datastore.
     *
     * @return An instance of {@link Transfer} that was created by this method.
     */
    //Transfer createTransfer();

    /**
     * Submit a transferFunds to this ledger by validating it and saving it to an attached datastore.
     *
     * @param transferInput An instance of {@link TransferInput} to construct a {@link Transfer} toLedgerId.
     * @return An instance of {@link Transfer} that was created by this method.
     */
    Transfer createTransfer(final TransferInput transferInput);

    /**
     * Submit a transferFunds to this ledger by validating it and saving it to an attached datastore.
     *
     * @param transferId An instance of {@link LedgerTransferId} that identifies a {@link Transfer} to prepare.
     * @return An instance of {@link Transfer} that was created by this method.
     */
   // Transfer prepareTransfer(final TransferId transferId);

    /**
     * Get an optionally-present {@link Transfer} by its identifier.
     *
     * @param ledgerTransferId
     * @return
     */
    Optional<Transfer> getTransfer(LedgerTransferId ledgerTransferId);

    /**
     * Update a {@link Transfer} with supplied inputs.
     *
     * @param ledgerTransferId
     * @param transferInput
     * @return
     */
    Transfer updateTransfer(LedgerTransferId ledgerTransferId, TransferInput transferInput);
}
