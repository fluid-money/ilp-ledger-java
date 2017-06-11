package money.fluid.ilp.ledger.datastore.jpa.subscriptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.datastore.jpa.subscriptions.SubscriptionEntity.SubscriptionKey;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.web.websocket.model.LedgerEventType;
import money.fluid.ilp.ledger.web.websocket.model.LedgerResourceType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

import static java.util.Objects.requireNonNull;

/**
 * An entity for storing Subscription information.
 *
 * @deprecated This was created to persist subscriptions, but the new methodology is to keep subscriptions in memory,
 * and force clients to re-subscribe when they reconnect.
 */
@Entity
@IdClass(SubscriptionKey.class)
@Getter
@ToString
@EqualsAndHashCode
@Deprecated
public class SubscriptionEntity {

    /**
     * The unique identifier of the subscriber.  Should have read or write access to the account in question.
     */
    @Id
    @Column(name = "SUBSCRIBER_ID", length = 36, nullable = false)
    private final String subscriberId;

    /**
     * The account that this subscriber is listening to events for.
     */
    @Id
    @Column(name = "ACCOUNT_ID", length = 36, nullable = false)
    private final String accountId;

    /**
     * The resource that this subscription listens to events for.
     */
    @Column(name = "RESOURCE", length = 36)
    private final String resource;

    /**
     * The resource that this subscription listens to events for.
     */
    @Column(name = "EVENT", length = 36)
    private final String event;

    /**
     * No-args Constructor.  Exists only for Hibernate...
     */
    SubscriptionEntity() {
        this.subscriberId = null;
        this.accountId = null;
        this.resource = null;
        this.event = null;
    }

    public SubscriptionEntity(final SubscriptionEntity.Builder builder) {
        requireNonNull(builder);

        this.subscriberId = requireNonNull(builder.subscriberId);
        this.accountId = requireNonNull(builder.accountId);
        this.resource = requireNonNull(builder.resource);
        this.event = requireNonNull(builder.event);
    }

    public SubscriptionKey getSubscriptionKey() {
        return new SubscriptionKey(this.getSubscriberId(), this.getAccountId());
    }

    @Embeddable
    @RequiredArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class SubscriptionKey implements Serializable {

        /**
         * The unique identifier of the subscriber.  This corresponds to a {@link LedgerAccountId}.
         */
        @Column(name = "SUBSCRIBER_ID", length = 36, nullable = false)
        private final String subscriberId;

        /**
         * The account that this subscriber is listening to events for.
         */
        @Column(name = "ACCOUNT_ID", length = 36, nullable = false)
        private final String accountId;

        /**
         * No-args Constructor.  Exists only for Hibernate...
         */
        SubscriptionKey() {
            this.subscriberId = null;
            this.accountId = null;
        }
    }

    @ToString
    public static class Builder {

        private String subscriberId;

        private String accountId;

        private String resource;

        private String event;

        public Builder() {
        }

        public Builder(final SubscriptionEntity entity) {
            requireNonNull(entity);

            this.subscriberId = entity.subscriberId;
            this.accountId = entity.accountId;
            this.resource = entity.resource;
            this.event = entity.event;
        }

        public SubscriptionEntity build() {
            return new SubscriptionEntity(this);
        }

        public Builder withSubscriberId(String subscriberId) {
            this.subscriberId = subscriberId;
            return this;
        }

        public Builder withAccountId(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder withResource(final LedgerResourceType ledgerResourceType) {
            this.resource = ledgerResourceType.name();
            return this;
        }

        public Builder withEvent(final LedgerEventType event) {
            this.event = event.name();
            return this;
        }
    }
}
