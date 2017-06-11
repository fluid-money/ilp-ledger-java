package money.fluid.ilp.ledger.events.handler;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.ilpx.ledger.core.AbstractLedgerEventHandler;
import org.ilpx.ledger.core.Ledger;
import org.ilpx.ledger.core.LedgerEventHandler;
import org.ilpx.ledger.core.events.LedgerConnectedEvent;
import org.ilpx.ledger.core.events.LedgerDisonnectedEvent;
import org.ilpx.ledger.core.events.LedgerEvent;
import org.ilpx.ledger.core.events.LedgerTransferCreatedEvent;
import org.ilpx.ledger.core.events.LedgerTransferExecutedEvent;
import org.ilpx.ledger.core.events.LedgerTransferRejectedEvent;
import org.ilpx.ledger.core.events.LedgerTransferUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Consumer;


/**
 * An extension of {@link AbstractLedgerEventHandler} that implements {@link LedgerEventHandler} and uses Guava's {@link
 * EventBus} to route all ILP ledger events.  Note that per {@link LedgerEventHandler}, this implementation listens to a
 * single ledger and listens on behalf of a single connector.
 *
 * @deprecated Won't be used in favor of SpringMessaging.
 */
@ToString
@EqualsAndHashCode
@Deprecated
public abstract class AbstractEventBusLedgerEventHandler extends AbstractLedgerEventHandler implements LedgerEventHandler<LedgerEvent> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Getter
    private final Ledger ledger;

    // Purposefully not exposed via Getter...
    private final EventBus eventBus;

    /**
     * Required-args Constructor.
     *
     * @param eventBus An instance of {@link EventBus} that can be custom-configured by the creator of this class.
     * @param ledger   TODO: document this!
     */
    public AbstractEventBusLedgerEventHandler(final EventBus eventBus, final Ledger ledger) {
        this.eventBus = Objects.requireNonNull(eventBus);
        this.ledger = Objects.requireNonNull(ledger);
        this.eventBus.register(this);
    }

    /**
     * Handles a {@link LedgerEvent} by posting it to the internal {@link EventBus} to allow it to process all
     * subscribed handlers.
     *
     * @param ledgerEvent An instance of {@link LedgerEvent}.
     */
    @Override
    protected final void handleInternal(final LedgerEvent ledgerEvent) {
        Preconditions.checkNotNull(ledgerEvent);
        eventBus.post(ledgerEvent);
    }

//    @Subscribe
//    private final void handleEventInternal(final LedgerConnectedEvent ledgerConnectedEvent) {
//        // Forward to handleEventInternalHelper for logging purposes...
//        this.handleEventInternalHelper((event) -> this.handleEvent(event), ledgerConnectedEvent);
//    }
//
//    @Subscribe
//    private final void handleEventInternal(final LedgerDisonnectedEvent ledgerDisonnectedEvent) {
//        // Forward to handleEventInternalHelper for logging purposes...
//        this.handleEventInternalHelper((event) -> this.handleEvent(event), ledgerDisonnectedEvent);
//    }

    @Subscribe
    private final void handleEventInternal(final LedgerTransferCreatedEvent ledgerTransferCreatedEvent) {
        // Forward to handleEventInternalHelper for logging purposes...
        this.handleEventInternalHelper((event) -> this.handleEvent(event), ledgerTransferCreatedEvent);
    }

    @Subscribe
    private final void handleEventInternal(final LedgerTransferUpdatedEvent ledgerTransferUpdatedEvent) {
        // Forward to handleEventInternalHelper for logging purposes...
        this.handleEventInternalHelper((event) -> this.handleEvent(event), ledgerTransferUpdatedEvent);
    }

//    @Subscribe
//    private final void handleEventInternal(final LedgerTransferExecutedEvent ledgerTransferExecutedEvent) {
//        // Forward to handleEventInternalHelper for logging purposes...
//        this.handleEventInternalHelper((event) -> this.handleEvent(event), ledgerTransferExecutedEvent);
//    }
//
//    @Subscribe
//    private final void handleEventInternal(final LedgerTransferRejectedEvent ledgerTransferRejectedEvent) {
//        // Forward to handleEventInternalHelper for logging purposes...
//        this.handleEventInternalHelper((event) -> this.handleEvent(event), ledgerTransferRejectedEvent);
//    }

    /**
     * Helper method that logs around the supplied {@link Consumer}.
     *
     * @param consumer
     * @param ledgerEvent
     * @param <T>
     */
    private final <T extends LedgerEvent> void handleEventInternalHelper(
            final Consumer<T> consumer, final T ledgerEvent
    ) {
        logger.info(
                "LedgerEventHandler[{}]: About to handle LedgerEvent '{}' for Listener[{}]",
                this.getLedger().getLedgerInfo().getLedgerId(),
                ledgerEvent,
                this.getListenerId()
        );
        consumer.accept(ledgerEvent);
        logger.info(
                "LedgerEventHandler[{}]: Handled LedgerEvent '{}' for Listener[{}]",
                this.getLedger().getLedgerInfo().getLedgerId(),
                ledgerEvent,
                this.getListenerId()
        );
    }

    @Subscribe
    protected void deadEvent(final DeadEvent deadEvent) {
        throw new RuntimeException("Unhandled Event: " + deadEvent);
    }

}
