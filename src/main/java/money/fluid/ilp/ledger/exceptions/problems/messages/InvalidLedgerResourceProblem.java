package money.fluid.ilp.ledger.exceptions.problems.messages;

import org.zalando.problem.AbstractThrowableProblem;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static money.fluid.ilp.ledger.exceptions.problems.ProblemConstants.PROBLEM_URI_PREFIX;

public class InvalidLedgerResourceProblem extends AbstractThrowableProblem {

    static final URI TYPE = URI.create(PROBLEM_URI_PREFIX + "/invalid-ledger-resource");

    /**
     * Required-args Constructor.
     *
     * @param invalidLedgerResource A {@link String} that contains the invalid ledger resource, as specified by an
     *                              external client.
     */
    public InvalidLedgerResourceProblem(final String invalidLedgerResource) {
        super(TYPE, "Invalid LedgerResource", BAD_REQUEST, invalidLedgerResource);
    }
}