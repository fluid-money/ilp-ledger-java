package money.fluid.ilp.ledger.web.controllers;

import money.fluid.ilp.ledger.exceptions.problems.transfers.AccountNotFoundProblem;
import money.fluid.ilp.ledger.model.LedgerAccount;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.web.factories.AccountRepresentationFactory;
import money.fluid.ilp.ledger.web.model.AccountRepresentation;
import money.fluid.ledger.datastore.spring.AccountRepositoryCustom;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Get an account resource.
 */
@RestController
public class AccountController {

    public static final String ACCOUNT_ID = "account_id";

    private final AccountRepositoryCustom accountRepository;

    private final AccountRepresentationFactory accountRepresentationFactory;

    public AccountController(
            final AccountRepositoryCustom accountRepository,
            final AccountRepresentationFactory accountRepresentationFactory
    ) {
        this.accountRepository = accountRepository;
        this.accountRepresentationFactory = accountRepresentationFactory;
    }


    /**
     * Get an {@link AccountRepresentation}.
     *
     * @return An instance of {@link AccountRepresentation}
     */
    @RequestMapping(
            path = "/accounts/{" + ACCOUNT_ID + "}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public AccountRepresentation getTransfer(@PathVariable(ACCOUNT_ID) final String accountID) {
        final LedgerAccount ledgerAccount = this.accountRepository.getAccount(
                LedgerAccountId.of(accountID)).orElseThrow(
                () -> new AccountNotFoundProblem(LedgerAccountId.of(accountID)));
        return this.accountRepresentationFactory.construct(ledgerAccount);
    }
}
