package org.ilpx.ledger.core;

import org.ilpx.core.LedgerEventListener;
import org.ilpx.ledger.core.events.LedgerEvent;

/**
 * An interface for managing all instances of {@link LedgerEventListener} that have been created for each client
 * listening to events propagated by a ledger.
 */
public interface LedgerEventPublisher {

    // A Map of LedgerEventListeners, associated to a particular

    // TODO: Determines who and what type of event to publish - e.g., AccountEvent vs TransferEvent.
    void publish(LedgerEvent ledgerEvent);

//    void publishLedgerAccountEvent(LedgerAccountId ledgerAccountId, LedgerEvent ledgerEvent);

//    void publishTransferEvent(LedgerAccountId ledgerAccountId, LedgerEvent ledgerEvent);

    // publishMessageEvent?
}