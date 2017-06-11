package money.fluid.ilp.ledger.datastore.jpa.subscriptions;

import money.fluid.ilp.ledger.datastore.jpa.subscriptions.SubscriptionEntity.SubscriptionKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * A repository class that provides APIs to do CRUD operations and some find operations like findAll, findOne, count.
 *
 * @deprecated This was created to persist subscriptions, but the new methodology is to keep subscriptions in memory,
 * and force clients to re-subscribe when they reconnect.
 */
@Repository
@Deprecated
public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, SubscriptionKey> {
    //List<SubscriptionEntity> findBySubscriberId(String subscriberId);

    List<SubscriptionEntity> findBySubscriberIdAndResource(String subscriberId, String resource);
}