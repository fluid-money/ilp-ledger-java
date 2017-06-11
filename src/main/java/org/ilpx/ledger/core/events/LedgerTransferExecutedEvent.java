package org.ilpx.ledger.core.events;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;

import java.math.BigDecimal;

/**
 * @deprecated CLAPI has only two events: created, and updated.  The status of a transferFunds is then inspected to see what
 * happened.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class LedgerTransferExecutedEvent extends LedgerTransferEvent {

    private static final long serialVersionUID = 2742406317777118624L;

    public LedgerTransferExecutedEvent(
            final LedgerTransferId ledgerTransferId,
            final LedgerInfo source,
            final Object ilpPacket,
            final LedgerAccountId ledgerAccountId,
            final BigDecimal localTransferAmount
    ) {
        super(source, ledgerTransferId, ilpPacket, ledgerAccountId, localTransferAmount);
    }

}
