package money.fluid.ilp.ledger.web.controllers;

import money.fluid.ilp.ledger.exceptions.problems.messages.MessageRequiredProblem;
import money.fluid.ilp.ledger.web.model.MessageRepresentation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.HeadersBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get an account resource.
 */
@RestController
public class MessagesController {

    /**
     * Try to send a notification to another account.
     * <p>
     * The message is only delivered if the other account is subscribed to account notifications on a WebSocket
     * connection. ILP Clients and ILP Connectors use this method to request and send quotes.
     * <p>
     * Authorization: The from field of the message MUST match the account owner. Unauthenticated users MUST NOT be able
     * to send messages. A ledger MAY allow admin connections to send messages whose from value matches the ledger's
     * base URL.
     */
    @RequestMapping(
            path = "/messages", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public HeadersBuilder<?> sendMessage(@RequestBody final MessageRepresentation messageRepresentation) {
        if (messageRepresentation == null) {
            throw new MessageRequiredProblem();
        }

        // TODO: Send the message to any subscriptions!

        // TODO Buffer messages for any unsubscribed accounts?

        return ResponseEntity.noContent();
    }
}
