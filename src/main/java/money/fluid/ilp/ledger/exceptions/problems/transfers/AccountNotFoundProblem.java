package money.fluid.ilp.ledger.exceptions.problems.transfers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;
import static money.fluid.ilp.ledger.web.controllers.AccountController.ACCOUNT_ID;

/**
 * Used when a client specifies an invalid {@link LedgerAccountId}.
 */
public final class AccountNotFoundProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/account-not-found");

    @Getter
    @JsonProperty(ACCOUNT_ID)
    private final LedgerAccountId accountId;

    public AccountNotFoundProblem(final LedgerAccountId accountId) {
        super(
                TYPE, "Account not found", NOT_FOUND,
                "No Account exists for the specified identifier."
        );
        this.accountId = requireNonNull(accountId);
    }
}
