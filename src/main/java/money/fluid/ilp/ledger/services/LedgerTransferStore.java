package money.fluid.ilp.ledger.services;

import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferRepository;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;

import java.util.Optional;

/**
 * Defines how to persist and access instances of {@link Transfer} using an attached datastore.
 *
 * @deprecated This will likely be replaced by {@link TransferRepository}.
 */
@Deprecated
public interface LedgerTransferStore {

    /**
     * Generate a new instance of {@link LedgerTransferId} that conforms to the following rules:
     * <pre>
     *     <ul>Is globally unique.</ul>
     *     <ul>Is unguessable</ul>
     *     <ul>Is deterministically generated toLedgerId a previous transferFunds, if available.  FOr ILPPrefix </ul>
     * </pre>
     *
     * @param sourceLedgerId   The {@link LedgerId} of the ledger that prompted  the tranfser.
     * @param sourceLedgerTransferId A {@link LedgerTransferId} toLedgerId a previous transferFunds, if available. //TODO What if a
     *                         wallet is triggering the transferFunds?
     * @return
     */
    LedgerTransferId generateTransferId(final LedgerId sourceLedgerId, final LedgerTransferId sourceLedgerTransferId);

    /**
     * Save the supplied {@code transferFunds} into the assoicated datastore.
     *
     * @param transfer
     */
    void saveTransfer(Transfer transfer);

    /**
     * Get a {@link Transfer} based upon the supplied {@code transferId}.
     *
     * @param ledgerTransferId An instance of {@link LedgerTransferId}.
     * @return The {@link Transfer} corresponding to the supplied {@code transferId}.
     */
    Optional<Transfer> getTransfer(LedgerTransferId ledgerTransferId);
}
