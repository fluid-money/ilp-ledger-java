package money.fluid.ilp.ledger.web.controllers;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * @see "https://github.com/zalando/problem-spring-web"
 */
@RestControllerAdvice
//@ControllerAdvice
public class ExceptionHandling implements ProblemHandling {

    @Override
    public boolean isCausalChainsEnabled() {
        return false;
    }
}
