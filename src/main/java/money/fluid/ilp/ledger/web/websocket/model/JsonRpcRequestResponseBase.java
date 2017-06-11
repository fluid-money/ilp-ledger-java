package money.fluid.ilp.ledger.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * A pojo for modeling a "Subscribe to Account" message.  Such a request replaces any existing account subscriptions on
 * this WebSocket connection.
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class JsonRpcRequestResponseBase extends JsonRpcBase {

    /**
     * An arbitrary identifier for this request. MUST NOT be null for subscription request payloads.
     * <p>
     * If this is a request (e.g., subscription request), then the immediate response to this request identifies itself
     * using the same id.
     */
    @JsonProperty("id")
    private final String id;

    /**
     * Required-args Constructor.
     *
     * @param id An instance of {@link String} that uniquely identifies a request.
     */
    public JsonRpcRequestResponseBase(final String id) {
        // Will be absent for notifications, but present for requests.
        this.id = Objects.requireNonNull(id);
    }

}
