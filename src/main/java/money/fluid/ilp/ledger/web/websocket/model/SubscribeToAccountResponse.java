package money.fluid.ilp.ledger.web.websocket.model;

import com.fasterxml.jackson.annotation.JsonCreator;
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
public class SubscribeToAccountResponse extends JsonRpcRequestResponseBase {

    /**
     * Updated number of active transferFunds subscriptions on this WebSocket connection. In practice, this is usually the
     * length of the params.transfers array from the request.
     */
    @JsonProperty("result")
    private final Integer result;

    /**
     * Required-args Constructor.
     *
     * @param id An instance of {@link String} that uniquely identifies a request.
     */
    @JsonCreator
    public SubscribeToAccountResponse(
            @JsonProperty("id") final String id, @JsonProperty("result") final Integer result
    ) {
        super(id);
        this.result = result;
    }
}
