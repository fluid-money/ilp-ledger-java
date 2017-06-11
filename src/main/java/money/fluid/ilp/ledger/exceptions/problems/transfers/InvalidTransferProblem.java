package money.fluid.ilp.ledger.exceptions.problems.transfers;

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
public final class InvalidTransferProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/invalid-transferFunds");

    @Getter
    @JsonProperty("source_account_id")
    private final LedgerAccountId sourceAccountId;

    @Getter
    @JsonProperty("destination_account_id")
    private final LedgerAccountId destinationAccountId;

    public InvalidTransferProblem(
            final LedgerAccountId sourceAccountId,
            final LedgerAccountId destinationAccountId,
            final String message
    ) {
        super(
                TYPE, "Invalid Transfer", BAD_REQUEST, message
        );

        this.sourceAccountId = requireNonNull(sourceAccountId);
        this.destinationAccountId = requireNonNull(destinationAccountId);
    }
}