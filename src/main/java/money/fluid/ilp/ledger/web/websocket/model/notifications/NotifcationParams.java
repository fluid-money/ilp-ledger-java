package money.fluid.ilp.ledger.web.websocket.model.notifications;

import money.fluid.ilp.ledger.web.websocket.model.LedgerResourceType;

import java.util.Optional;

/**
 * A pojo for modeling information about what subscriptions to open.
 *
 * @param <E> The type of event this object contains.
 */
public interface NotifcationParams<E> {

    /**
     * (Optional) A UUID to uniquely identify this notification.
     */
    Optional<String> getId();

    /**
     * The type of event that prompted this notification. Valid types include transferFunds.create, transferFunds.update, and
     * others. See {@link LedgerResourceType} for more information.
     */
    E getEvent();

    /**
     * An object related to event that occurred. In most cases, this is a Transfer resource as it was created or
     * updated.
     */
    // TODO: Make this generic when fulfilments are introduced.
    String getResource();

    /**
     * (Not present in all responses) Contains additional resources related to this notification in named sub-fields,
     * depending on the event type.   In particular, this MUST contain the fulfillment when a transferFunds is updated to the
     * executed state.
     *
     * @return
     */
    // TODO: Strongly type this?
    Optional<String> getRelatedResource();

}
