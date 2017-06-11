package money.fluid.ilp.ledger.web.websocket.model;

import lombok.Getter;

import java.util.Objects;

/**
 * An enum of Ledger resources, used to identify the resource that a given subscription or event applies to.
 */
public enum LedgerResourceType {
    ACCOUNT("account"),
    TRANSFER("transferFunds");

    @Getter
    private final String value;

    LedgerResourceType(final String value) {
        this.value = Objects.requireNonNull(value);
    }
}
