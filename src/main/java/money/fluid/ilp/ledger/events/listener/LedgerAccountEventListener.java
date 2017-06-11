package money.fluid.ilp.ledger.events.listener;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import org.ilpx.core.LedgerEventListener;
import org.ilpx.ledger.core.LedgerEventHandler;
import org.ilpx.ledger.core.events.LedgerConnectedEvent;
import org.ilpx.ledger.core.events.LedgerEvent;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * An implementation of {@link LedgerEventListener} that allows a subscriber to listen for events related to a single
 * {@link LedgerAccountId}.
 */
@Getter
@RequiredArgsConstructor
public class LedgerAccountEventListener implements LedgerEventListener {

    /**
     * Info about the ledger that this listener is listening to.
     */
    @NonNull
    private final LedgerInfo ledgerInfo;

    /**
     * The {@link String} identifier for the subscriber listening for events for the designated account.
     */
    @NonNull
    private final String subscriberId;

    /**
     * The {@link LedgerAccountId} for which this listener is listening to events for.
     */
    @NonNull
    private final LedgerAccountId listeningAccountId;

    /**
     * This listener might have a single handler (for all events) or it might have a single handler for each event.
     * However, this always only connects a single ledger/connector pair.
     */
    @NonNull
    private final Set<LedgerEventHandler> ledgerEventHandlers;

    /**
     * Helper Constructor.  Initializes the {@link List} of {@link LedgerEventHandler}'s to an empty list.
     *
     * @param ledgerInfo
     * @param listeningAccountId
     */
    public LedgerAccountEventListener(
            final LedgerInfo ledgerInfo, final String subscriberId, final LedgerAccountId listeningAccountId
    ) {
        this.ledgerInfo = Objects.requireNonNull(ledgerInfo);
        this.subscriberId = Objects.requireNonNull(subscriberId);
        this.listeningAccountId = Objects.requireNonNull(listeningAccountId);
        this.ledgerEventHandlers = new ConcurrentSkipListSet<>();
    }

    // TODO: consider returning a boolean to align with java.util.Collection - indicates if the handler already existed?
    @Override
    public void registerEventHandler(LedgerEventHandler<?> handler) {
        Preconditions.checkNotNull(handler);

        // Add the handler to the list...
        this.ledgerEventHandlers.add(handler);

        // Any handlers in this class should be notified, since they're all limited to a single connector.
        this.notifyEventHandlers(new LedgerConnectedEvent(this.getLedgerInfo()));
    }

    @Override
    public void unRegisterEventHandlers() {
        // TODO: Is this necessary?  There's a contradiction between a Connector being disconnected from a ledger and
        // a ledger trying to send the disconnected Connector an event that says the disconnect worked.
        //this.notifyEventHandlers(new LedgerDisonnectedEvent(this.getLedgerInfo(), this.getConnectorInfo()));

        // Remove all envet handlers...
        this.ledgerEventHandlers.clear();
    }

    @Override
    public void unRegisterEventHandler(LedgerEventHandler<?> handler) {
        // Remove the handler from the handler list...
        this.ledgerEventHandlers.remove(handler);
    }

    @Override
    public void notifyEventHandlers(final LedgerEvent ledgerEvent) {
        // Notify all handlers.  If this notify handler is getting called, it means there's an event for this ledger/connector combination.
        for (final LedgerEventHandler handler : this.ledgerEventHandlers) {
            handler.onLedgerEvent(ledgerEvent);
        }
    }
}