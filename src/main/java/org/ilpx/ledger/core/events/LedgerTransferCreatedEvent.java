package org.ilpx.ledger.core.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;

import java.math.BigDecimal;

/**
 * An event that is sent by a ledger when a transferFunds has been created.
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LedgerTransferCreatedEvent extends LedgerTransferEvent {

    private static final long serialVersionUID = 4965377395551079045L;

    /**
     * No-args Constructor.  Exists only for Jackson compatibility.
     */
    LedgerTransferCreatedEvent() {
    }

    public LedgerTransferCreatedEvent(
            final LedgerTransferId ledgerTransferId,
            final LedgerInfo source,
            final Object ilpPacketHeader,
            final LedgerAccountId ledgerAccountId,
            final BigDecimal localTransferAmount
    ) {
        super(source, ledgerTransferId, ilpPacketHeader, ledgerAccountId, localTransferAmount);
    }
}
