package money.fluid.ilp.ledger.exceptions.problems;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

/**
 * Used when a client specifies an invalid {@link LedgerTransferId}.
 */
public final class InvalidLedgerAccountAmountProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/transferFunds-not-found");

    @Getter
    @JsonProperty("ledger_account_id")
    private final LedgerAccountId ledgerAccountId;

    public InvalidLedgerAccountAmountProblem(final LedgerAccountId ledgerAccountId) {
        super(
                TYPE, "Insufficient Account Balance", BAD_REQUEST,
                String.format(
                        "LedgerAccount with Id '%s' did not have enough funds to complete this request.",
                        ledgerAccountId
                )
        );
        this.ledgerAccountId = requireNonNull(ledgerAccountId);
    }
}