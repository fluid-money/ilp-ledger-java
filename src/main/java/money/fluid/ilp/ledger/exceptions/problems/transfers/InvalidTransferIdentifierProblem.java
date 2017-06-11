package money.fluid.ilp.ledger.exceptions.problems.transfers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

/**
 * Used when a client specifies an invalid {@link LedgerTransferId}.
 */
public final class InvalidTransferIdentifierProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/invalid-transferFunds-identifier");

    @Getter
    @JsonProperty("transfer_id")
    private final LedgerTransferId ledgerTransferId;

    public InvalidTransferIdentifierProblem(final LedgerTransferId ledgerTransferId) {
        super(
                TYPE, "Invalid TransferId", BAD_REQUEST,
                String.format(
                        "Client-supplied transfer_id is not valid.  Must be a type-4 UUID.",
                        ledgerTransferId
                )
        );
        this.ledgerTransferId = requireNonNull(ledgerTransferId);
    }
}
