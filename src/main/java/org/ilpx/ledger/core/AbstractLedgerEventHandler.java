package org.ilpx.ledger.core;

import com.google.common.base.Preconditions;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.ilpx.ledger.core.events.LedgerEvent;
import org.ilpx.ledger.core.events.LedgerTransferCreatedEvent;
import org.ilpx.ledger.core.events.LedgerTransferUpdatedEvent;

/**
 * An abstract implementation of {@link LedgerEventHandler} that handles all variants of {@link LedgerEvent} in a
 * type-safe manner, and provides a mechanism to detect unhandled event-types at runtime.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class AbstractLedgerEventHandler implements LedgerEventHandler<LedgerEvent> {

    @Override
    public final void onLedgerEvent(final LedgerEvent ledgerEvent) {
        this.handleInternal(ledgerEvent);
    }

    /**
     * Handles a {@link LedgerEvent} in a type-safe fashion and accounts for unhandled events.
     *
     * @param ledgerEvent An instance of {@link LedgerEvent}.
     */
    protected void handleInternal(final LedgerEvent ledgerEvent) {
        Preconditions.checkNotNull(ledgerEvent);

        // If this were a more recent version of Java, this could be a Switch statement...
//        if (ledgerEvent.getClass().equals(LedgerConnectedEvent.class)) {
//            this.handleEvent((LedgerConnectedEvent) ledgerEvent);
//        } else if (ledgerEvent.getClass().equals(LedgerDisonnectedEvent.class)) {
//            this.handleEvent((LedgerDisonnectedEvent) ledgerEvent);
//        }
//        else
        if (ledgerEvent.getClass().equals(LedgerTransferCreatedEvent.class)) {
            this.handleEvent((LedgerTransferCreatedEvent) ledgerEvent);
        } else if (ledgerEvent.getClass().equals(LedgerTransferUpdatedEvent.class)) {
            this.handleEvent((LedgerTransferUpdatedEvent) ledgerEvent);
        }
//        else if (ledgerEvent.getClass().equals(LedgerTransferExecutedEvent.class)) {
//            this.handleEvent((LedgerTransferExecutedEvent) ledgerEvent);
//        }
//        else if (ledgerEvent.getClass().equals(LedgerTransferRejectedEvent.class)) {
//            this.handleEvent((LedgerTransferRejectedEvent) ledgerEvent);
//        }
        else {
            this.handleUnhandledEvent(ledgerEvent);
        }
    }

//    /**
//     * Handle an instance of {@link LedgerConnectedEvent}.
//     *
//     * @param ledgerConnectedEvent An instance of {@link LedgerConnectedEvent}.
//     */
//    protected abstract void handleEvent(final LedgerConnectedEvent ledgerConnectedEvent);
//
//    /**
//     * Handle an instance of {@link LedgerDisonnectedEvent}.
//     *
//     * @param ledgerDisonnectedEvent An instance of {@link LedgerDisonnectedEvent}.
//     */
//    protected abstract void handleEvent(final LedgerDisonnectedEvent ledgerDisonnectedEvent);

    /**
     * Handle an instance of {@link LedgerTransferCreatedEvent}.
     *
     * @param ledgerTransferCreatedEvent An instance of {@link LedgerTransferCreatedEvent}.
     */
    protected abstract void handleEvent(final LedgerTransferCreatedEvent ledgerTransferCreatedEvent);

    /**
     * Handle an instance of {@link LedgerTransferUpdatedEvent}.
     *
     * @param ledgerTransferUpdatedEvent An instance of {@link LedgerTransferUpdatedEvent}.
     */
    protected abstract void handleEvent(final LedgerTransferUpdatedEvent ledgerTransferUpdatedEvent);

//    /**
//     * Handle an instance of {@link LedgerTransferExecutedEvent}.
//     *
//     * @param ledgerTransferExecutedEvent An instance of {@link LedgerTransferExecutedEvent}.
//     */
//    protected abstract void handleEvent(final LedgerTransferExecutedEvent ledgerTransferExecutedEvent);
//
//    /**
//     * Handle an instance of {@link LedgerTransferRejectedEvent}.
//     *
//     * @param ledgerTransferRejectedEvent An instance of {@link LedgerTransferRejectedEvent}.
//     */
//    protected abstract void handleEvent(final LedgerTransferRejectedEvent ledgerTransferRejectedEvent);

    /**
     * Throws a {@link RuntimeException} if an un-handled event is encountered.  Protected so that sub-classes can
     * override this behavior, if desired.
     *
     * @param ledgerEvent An instance of {@link LedgerEvent} that is unhandled by this
     * @return {@code true} if the event was handled; {@code false} otherwise.
     */
    protected void handleUnhandledEvent(final LedgerEvent ledgerEvent) {
        throw new RuntimeException("Unhandled Event: " + ledgerEvent);
    }
}
