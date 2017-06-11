package money.fluid.ilp.ledger.web.websocket.jsonrpc;

import money.fluid.ilp.ledger.web.websocket.model.SubscribeToAccountRequest;
import org.ilpx.ledger.core.events.LedgerEvent;
import org.ilpx.ledger.core.events.LedgerTransferCreatedEvent;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Handles ledger-generated events related to accounts.
 *
 * @see "https://docs.spring.io/spring/docs/current/spring-framework-reference/html/websocket.html"
 */
@Controller
public class AccountSubscriptionController {

    /**
     * Events sent to "/app/subscribe_account/{account-id}" pass through the "clientInboundChannel" and are forwarded to
     * this controller. The controller returns the {@link LedgerEvent}, and the return value is passed through the
     * "brokerChannel" as a message to "/topic/subscribe_account/{account-id}" (destination is selected based on a
     * convention but can be overridden via @SendTo).  The broker in turn broadcasts messages to subscribers, and they
     * pass through the "clientOutboundChannel".
     *
     * @return The return value from an @MessageMapping method is converted with a {@link MessageConverter} and used as
     * the body of a new message that is then sent, by default, to the "brokerChannel" with the same destination as the
     * client message but using the prefix "/topic" by default.
     */
//    @MessageMapping("/subscribe_account/{account-id}")
//    //@SendTo("/topic/subscribe_account")
//    public LedgerEvent handleLedgerEvent(
//            @Payload final LedgerEvent ledgerEvent,
//            @DestinationVariable("account-id") final String accountId
//            // Principal user
//    ) {
//        return ledgerEvent;
//    }
//
//    @MessageMapping("/subscribe_account")
//    //@SendTo("/topic/subscribe_account")
//    public LedgerEvent handleLedgerEventGlobal(
//            @Payload final SubscribeToAccountRequest subscribeToAccountRequest
//    ) {
//        return null;
//    }

    // TODO: Consider sending the Transfer to this method, and then send the response to the User?

    // To target this method, send via the OutboundChannel.
    // Responses from this @MessageMapping will be sent to /topic/transfer_created, unless overridden by @SendTo.
    @MessageMapping("/transfer_created")
    @SendTo("/queue/account_updates")
    public LedgerEvent handleLedgerEventGlobal(
            final Principal principal,
            @Payload final LedgerTransferCreatedEvent ledgerTransferCreatedEvent
    ) {
        return null;
    }


}
