package money.fluid.ilp.ledger.web.websocket.model.notifications;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A JsonRpc 2.0 payload for sending to connected Websocket clients when a transferFunds is created.
 */
@Getter
@EqualsAndHashCode
@ToString
public class TransferCreatedNotification extends AbstractTransferNotification {

    /**
     * Exists only for Jackson.
     */
    private TransferCreatedNotification() {
    }

    /**
     * Required Args Constructor.
     *
     * @param params An instance of {@link NotifcationParams} with more information about this request.
     */
    public TransferCreatedNotification(final TransferNotificationParams params) {
        super(params);
    }
}
