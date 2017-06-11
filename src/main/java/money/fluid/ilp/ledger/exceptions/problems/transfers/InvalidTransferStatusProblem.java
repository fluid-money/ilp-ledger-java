package money.fluid.ilp.ledger.exceptions.problems.transfers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import money.fluid.ilp.ledger.model.Transfer.Status;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

/**
 * Used when a client attempts to update a Transfer whose Status makes it (the transferFunds) ineligible to be updated.
 */
public final class InvalidTransferStatusProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/invalid-transferFunds-status");
    private static final String INVALID_TRANSFER_STATUS = "Invalid Transfer Status";

    @Getter
    @JsonProperty("transfer_id")
    private final LedgerTransferId ledgerTransferId;

    @Getter
    @JsonProperty("transfer_status")
    private final Status transferStatus;

    public InvalidTransferStatusProblem(
            final LedgerTransferId ledgerTransferId, final Status transferStatus
    ) {
        super(
                TYPE,
                INVALID_TRANSFER_STATUS,
                BAD_REQUEST,
                transferStatus.name()
        );
        this.ledgerTransferId = requireNonNull(ledgerTransferId);
        this.transferStatus = requireNonNull(transferStatus);
    }
}