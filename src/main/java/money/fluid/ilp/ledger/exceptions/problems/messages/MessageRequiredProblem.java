package money.fluid.ilp.ledger.exceptions.problems.messages;

import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

public class MessageRequiredProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/message-required");

    public MessageRequiredProblem() {
        super(
                TYPE, "Message Required", BAD_REQUEST, "A message is required for sending"
        );
    }
}