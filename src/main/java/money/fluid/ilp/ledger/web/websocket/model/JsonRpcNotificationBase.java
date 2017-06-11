package money.fluid.ilp.ledger.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A pojo for modeling a "Subscribe to Account" message.  Such a request replaces any existing account subscriptions on
 * this WebSocket connection.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class JsonRpcNotificationBase extends JsonRpcBase {
    protected static final String NOTIFY = "notify";

    /**
     * An arbitrary identifier for this request. MUST be null for notification payloads.
     */
    @JsonProperty("id")
    private final String id = null;

}
