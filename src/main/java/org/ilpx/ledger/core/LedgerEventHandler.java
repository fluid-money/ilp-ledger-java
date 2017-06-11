package org.ilpx.ledger.core;

import org.ilpx.ledger.core.events.LedgerEvent;

/**
 * An interface that defines how to handle an instance of {@link LedgerEvent}.  Implementations of this interface are
 * meant to handle events from a single emitting {@link Ledger}.
 * <p>
 * This interface allows implementations to be created that will handle only a single type of {@link LedgerEvent}, as
 * well as a single implementation that will handle all types of {@link LedgerEvent}.
 *
 * @param <T>
 */
public interface LedgerEventHandler<T extends LedgerEvent> {
    /**
     * Emits {@code event} to this handler for processing by any subscribers.
     *
     * @param event
     */
    void onLedgerEvent(T event);

    /**
     * A unique identifier for the system listening for events.
     *
     * @return A a unique listener id as a {@link String}.
     * @deprecated Is this required?  The listenerId will be registered with the Listener, not the handler.
     */
    // TODO: Is this required?  The listenerId will be registered with the Listener, not the handler.
    String getListenerId();
}
