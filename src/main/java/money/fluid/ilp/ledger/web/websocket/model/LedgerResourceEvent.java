package money.fluid.ilp.ledger.web.websocket.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ledger MAY define custom ledgerEventType types, but a ledger MUST support at least the following ledgerEventType
 * values:
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class LedgerResourceEvent {
    private static final Logger logger = LoggerFactory.getLogger(LedgerResourceEvent.class);

    @NonNull
    private final LedgerResourceType ledgerResourceType;
    @NonNull
    private final LedgerEventType ledgerEventType;
}