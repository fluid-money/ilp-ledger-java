package money.fluid.ilp.ledger.web.websocket.model.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.web.websocket.model.LedgerResourceType;

import java.util.Optional;

/**
 * An implementation of {@link NotifcationParams} for use in a message relating to a transferFunds status update.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class TransferNotificationParams implements NotifcationParams<String> {

    /**
     * (Optional) A UUID to uniquely identify this notification.
     */
    @JsonProperty("id")
    private final Optional<String> id;

    /**
     * The type of event that prompted this notification. Valid types include 'transferFunds.create', 'transferFunds.update', and
     * others. See {@link LedgerResourceType} for more information.
     */
    @JsonProperty("event")
    private final String event;

    /**
     * An object related to the event that occurred. In most cases, this is a Transfer resource as it was created or
     * updated.
     */
    @JsonProperty("resource")
    // TODO: Make this generic when fulfilments are introduced.
    private final String resource;

    /**
     * (Not present in all responses) Contains additional resources related to this notification in named sub-fields,
     * depending on the event type. In particular, this MUST contain the fulfillment when a transferFunds is updated to the
     * executed state.
     */
    @JsonProperty("related_resource")
    private final Optional<String> relatedResource;
}
