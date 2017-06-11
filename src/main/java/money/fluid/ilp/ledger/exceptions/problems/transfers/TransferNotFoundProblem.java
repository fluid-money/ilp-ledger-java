package money.fluid.ilp.ledger.exceptions.problems.transfers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

/**
 * Used when a client specifies an invalid {@link LedgerTransferId}.
 */
public final class TransferNotFoundProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/transferFunds-not-found");

    @Getter
    @JsonProperty("transfer_id")
    private final LedgerTransferId ledgerTransferId;

    public TransferNotFoundProblem(final LedgerTransferId ledgerTransferId) {
        super(
                TYPE, "Transfer not found", NOT_FOUND,
                String.format(
                        "No Transfer exists for the specified identifier.",
                        ledgerTransferId
                )
        );
        this.ledgerTransferId = requireNonNull(ledgerTransferId);
    }
}
