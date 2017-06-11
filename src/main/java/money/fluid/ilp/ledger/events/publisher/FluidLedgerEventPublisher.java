package money.fluid.ilp.ledger.events.publisher;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging.JsonRpcSubProtocolHandler;
import money.fluid.ilp.ledger.web.websocket.model.LedgerEventType;
import org.ilpx.ledger.core.AbstractLedgerEventHandler;
import org.ilpx.ledger.core.LedgerEventPublisher;
import org.ilpx.ledger.core.events.LedgerEvent;
import org.ilpx.ledger.core.events.LedgerTransferCreatedEvent;
import org.ilpx.ledger.core.events.LedgerTransferUpdatedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static money.fluid.ilp.ledger.web.websocket.jsonrpc.messaging.MessageHeaders.*;

/**
 * Publishes events to a Guava {@link EventBus} for async handling.
 */
@Service
public class FluidLedgerEventPublisher implements LedgerEventPublisher {

    /**
     * A simple template that can send messages to a simple Spring Message broker, which handles subscription requests
     * from clients, stores them in memory, and broadcasts messages to connected clients with matching destinations.
     */
    private final SimpMessagingTemplate template;

    /**
     * Required-args Constructor.
     */
    public FluidLedgerEventPublisher(final SimpMessagingTemplate template) {
        this.template = Objects.requireNonNull(template);
    }

    @Override
    public void publish(final LedgerEvent ledgerEvent) {
        new AbstractLedgerEventHandler() {
//            @Override
//            protected void handleEvent(LedgerConnectedEvent ledgerConnectedEvent) {
//
//            }
//
//            @Override
//            protected void handleEvent(LedgerDisonnectedEvent ledgerDisonnectedEvent) {
//
//            }

            @Override
            protected void handleEvent(final LedgerTransferCreatedEvent ledgerTransferCreatedEvent) {
                final Map<String, Object> headers = ImmutableMap.<String, Object>builder()
                        // Required for unmarshaling and subscription selector handling...
                        .put(LEDGER_EVENT_TYPE, LedgerEventType.TRANSFER_CREATE.getValue())
                        .put(LEDGER_ACCOUNT_ID, ledgerTransferCreatedEvent.getLedgerAccountId().value())
                        .build();

                template.convertAndSend(
                        JsonRpcSubProtocolHandler.DESTINATION__TRANSFER,
                        ledgerTransferCreatedEvent,
                        headers
                );
            }

            @Override
            protected void handleEvent(final LedgerTransferUpdatedEvent ledgerTransferUpdatedEvent) {
                final Map<String, Object> headers = ImmutableMap.<String, Object>builder()
                        // Required for unmarshaling and subscription selector handling...
                        .put(LEDGER_EVENT_TYPE, LedgerEventType.TRANSFER_UPDATE.getValue())
                        .put(LEDGER_ACCOUNT_ID, ledgerTransferUpdatedEvent.getLedgerAccountId().value())
                        .build();

                template.convertAndSend(
                        JsonRpcSubProtocolHandler.DESTINATION__TRANSFER,
                        ledgerTransferUpdatedEvent,
                        headers
                );
            }

//            /**
//             * For unconditional transfers, this occurs when the transferFunds is created.  For conditional transfers, if the
//             * transferFunds was rejected, the related_resources field must be empty.
//             *
//             * @param ledgerTransferExecutedEvent An instance of {@link LedgerTransferExecutedEvent}.
//             */
//            @Override
//            protected void handleEvent(final LedgerTransferExecutedEvent ledgerTransferExecutedEvent) {
//                Objects.requireNonNull(ledgerTransferExecutedEvent);
//                final Map<String, Object> headers = ImmutableMap.<String, Object>builder()
//                        // Only required so that the messaging handler can determine which type of payload to unmarshal to...
//                        .put(LEDGER_EVENT_TYPE, LedgerEventType.TRANSFER_CREATE.getValue())
//                        .build();
//
//                template.convertAndSend(
//                        JsonRpcSubProtocolHandler.DESTINATION__TRANSFER,
//                        ledgerTransferExecutedEvent,
//                        headers
//                );
//            }
//
//            @Override
//            protected void handleEvent(final LedgerTransferRejectedEvent ledgerTransferRejectedEvent) {
//                Objects.requireNonNull(ledgerTransferRejectedEvent);
//
//                final Map<String, Object> headers = ImmutableMap.<String, Object>builder()
//                        // Only required so that the messaging handler can determine which type of payload to unmarshal to...
//                        .put(LEDGER_EVENT_TYPE, LedgerEventType.TRANSFER_CREATE.getValue())
//                        .build();
//
//                template.convertAndSend(
//                        JsonRpcSubProtocolHandler.DESTINATION__TRANSFER,
//                        ledgerTransferRejectedEvent,
//                        headers
//                );
//            }

            // TODO: Is this necessary?
            @Override
            public String getListenerId() {
                return null;
            }
        }.onLedgerEvent(ledgerEvent);
    }
}
