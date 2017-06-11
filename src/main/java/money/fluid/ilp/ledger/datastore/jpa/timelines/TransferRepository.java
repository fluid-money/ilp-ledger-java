package money.fluid.ilp.ledger.datastore.jpa.timelines;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository class that provides APIs to do CRUD operations and some find operations like findAll, findOne, count.
 */
public interface TransferRepository extends CrudRepository<TransferEntity, String> {
}
