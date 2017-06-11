package money.fluid.ilp.ledger.web.websocket.model.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.web.websocket.model.JsonRpcNotificationBase;

import static java.util.Objects.requireNonNull;

/**
 * A JsonRpc 2.0 payload for sending to connected Websocket clients when a transferFunds is created.
 */
@Getter
@EqualsAndHashCode
@ToString
public abstract class AbstractTransferNotification extends JsonRpcNotificationBase {

    @JsonProperty("method")
    private final String method;

    @JsonProperty("params")
    // TODO: Cast this to a LedgerResourceEvent
    private final TransferNotificationParams params;

    /**
     * Exists only for Jackson.
     */
    AbstractTransferNotification() {
        this.method = NOTIFY;
        this.params = null;
    }

    /**
     * Required Args Constructor.
     *
     * @param params An instance of {@link NotifcationParams} with more information about this request.
     */
    public AbstractTransferNotification(final TransferNotificationParams params) {
        this.method = NOTIFY;
        this.params = requireNonNull(params);
    }
}
