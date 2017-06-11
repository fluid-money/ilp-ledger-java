package money.fluid.ilp.ledger.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A pojo for modeling a "Subscribe to Account" message.  Such a request replaces any existing account subscriptions on
 * this WebSocket connection.
 */
@Getter
@ToString
@EqualsAndHashCode
@JsonPropertyOrder({
        "id", "jsonrpc", "method", "params",
})
public class SubscribeToAccountRequest extends JsonRpcRequestResponseBase {

    public static final String SUBSCRIBE_ACCOUNT = "subscribe_account";

    @JsonProperty("method")
    private final String method;

    @JsonProperty("params")
    private final Params params;

    /**
     * Required Args Constructor.
     *
     * @param id     An arbitrary identifier for this request. MUST NOT be null. The immediate response to this request
     *               identifies itself using the same id.
     * @param params An instance of {@link Params} with more information about this request.
     */
    public SubscribeToAccountRequest(
            @JsonProperty("id") final String id,
            @JsonProperty("params") final Params params
    ) {
        super(id);
        this.method = SUBSCRIBE_ACCOUNT;
        this.params = params;
    }

    /**
     * No Args Constructor, exists only for Jackson.
     */
    private SubscribeToAccountRequest() {
        super("");
        this.method = null;
        this.params = null;
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @JsonPropertyOrder({"event", "accounts"})
    public static class Params {

        /**
         * (Optional) Types of events to subscribe to. May contain a wildcard character. See Event Types for a full list
         * of types. If omitted, subscribes to all eventType types.
         */
        @JsonProperty("event")
        @NonNull
        private final Optional<String> eventType;

        /**
         * Each member of this array must be the URL of an account to subscribe to, as a string. This replaces existing
         * subscriptions; if the array length is zero, the client is unsubscribed from all account notifications.
         */
        @JsonProperty("accounts")
        @NonNull
        private final List<String> accountIds;

        public Params(
                @JsonProperty("event_type") final String ledgerEventType,
                @JsonProperty("accounts") final List<String> accountIds
        ) {
            this.eventType = Optional.ofNullable(ledgerEventType);
            this.accountIds = requireNonNull(accountIds);
        }

        /**
         * Helper method that translates a dot-notation event type into a typed equivalent.  For example,
         * "transferFunds.create" would be translated into a
         *
         * @return
         */
        @JsonIgnore
        public LedgerEventType getLedgerResourceEvent() {
            return this.eventType.map(LedgerEventType::parseLedgerEventType).orElse(LedgerEventType.ALL);

//            return this.eventType
//                    .map((eventTypeString) -> new LedgerResourceEvent(
//                            LedgerResourceType.TRANSFER,
//                            LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable(eventTypeString))
//                    ))
//                    .orElse(new LedgerResourceEvent(LedgerResourceType.TRANSFER, LedgerEventType.ALL));
        }
    }
}
