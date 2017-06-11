package money.fluid.ledger.datastore.spring;

import money.fluid.ledger.datastore.jpa.AccountEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * A repository class that provides APIs to do CRUD operations and some find operations like findAll, findOne, count.
 */
public interface AccountRepository extends CrudRepository<AccountEntity, String>, AccountRepositoryCustom {

}
