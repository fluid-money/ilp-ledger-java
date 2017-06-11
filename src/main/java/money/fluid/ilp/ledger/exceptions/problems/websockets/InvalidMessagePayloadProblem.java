package money.fluid.ilp.ledger.exceptions.problems.websockets;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static java.util.Objects.requireNonNull;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

/**
 * Used when a client specifies an invalid {@link LedgerAccountId}.
 */
public final class InvalidMessagePayloadProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/invalid-websocket-message-payload");

    @Getter
    @JsonProperty("propertyName")
    private final String propertyName;

    public InvalidMessagePayloadProblem(final String propertyName) {
        super(
                TYPE, "Invalid Message Payload", BAD_REQUEST
        );
        this.propertyName = requireNonNull(propertyName);
    }
}
