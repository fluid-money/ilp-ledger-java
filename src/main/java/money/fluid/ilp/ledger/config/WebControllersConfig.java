package money.fluid.ilp.ledger.config;

import org.springframework.context.annotation.ComponentScan;

/**
 * Includes all Spring Controllers that display HTML.
 */
@ComponentScan({
        "money.fluid.ledger.web.controllers",
})
public class WebControllersConfig {
}
