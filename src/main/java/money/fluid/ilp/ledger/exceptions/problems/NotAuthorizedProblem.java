package money.fluid.ilp.ledger.exceptions.problems;

import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

/**
 * Used when a client makes an unauthorized request.
 */
public final class NotAuthorizedProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/not-authorized");

    public NotAuthorizedProblem() {
        super(TYPE, "Unauthorized", UNAUTHORIZED);
    }
}