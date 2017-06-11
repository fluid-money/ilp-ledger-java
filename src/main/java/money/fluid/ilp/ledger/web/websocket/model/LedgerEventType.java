package money.fluid.ilp.ledger.web.websocket.model;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum LedgerEventType {

    TRANSFER_CREATE("transferFunds.create"), // Applies to Account and Transfer...
    TRANSFER_UPDATE("transferFunds.update"), // Applies to Account and Transfer...
    TRANSFER_ALL("transferFunds.*"), // Applies to Account and Transfer...
    MESSAGE_SEND("message.send"), // Applies to Account...
    ALL("*");  // Applies to Account and Transfer...

    private static final Logger logger = LoggerFactory.getLogger(LedgerResourceEvent.class);
    private static final String STAR = "*";

    @Getter
    private final String value;

    LedgerEventType(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Parse a {@link String} like "*" or "transferFunds.create" or "account.*" into a typed representation of the event.  If
     * the {@code ledgerEventString} is null or otherwise un-parsable, this method will return {@link
     * LedgerEventType#ALL}.
     *
     * @param ledgerEventString
     * @return
     */
    public static LedgerEventType parseLedgerEventType(final String ledgerEventString) {
        return Optional.of(ledgerEventString).map(les -> {
            if (StringUtils.equalsIgnoreCase(les, STAR)) {
                return LedgerEventType.ALL;
            } else {
                return Arrays.stream(LedgerEventType.values())
                        .filter(let -> let.getValue().equalsIgnoreCase(les))
                        .findFirst()
                        .orElseGet(() -> {
                            logger.error("No constant with text {} found!", les);
                            return LedgerEventType.ALL;
                        });
            }
        }).orElse(LedgerEventType.ALL);
    }
}