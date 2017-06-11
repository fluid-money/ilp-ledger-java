package org.ilpx.ledger.core.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.ilpx.ledger.core.LedgerTransferRejectedReason;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @deprecated CLAPI has only two events: created, and updated.  The status of a transferFunds is then inspected to see what
 * happened.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class LedgerTransferRejectedEvent extends LedgerTransferEvent {

    private static final long serialVersionUID = 5106316912312360715L;

    private final LedgerTransferRejectedReason reason;

    public LedgerTransferRejectedEvent(
            final LedgerInfo source,
            final LedgerTransferId ledgerTransferId,
            final Object ilpPacketHeader,
            final LedgerAccountId ledgerAccountId,
            final BigDecimal localTransferAmount,
            final LedgerTransferRejectedReason ledgerTransferRejectedReason
    ) {
        super(source,ledgerTransferId, ilpPacketHeader, ledgerAccountId, localTransferAmount);
        this.reason = Objects.requireNonNull(ledgerTransferRejectedReason);
    }
}
