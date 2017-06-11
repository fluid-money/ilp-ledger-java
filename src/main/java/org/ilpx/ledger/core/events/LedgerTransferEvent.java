package org.ilpx.ledger.core.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;

import java.math.BigDecimal;
import java.util.Objects;

// Lombok only for quick prototyping...will remove if this makes it into ILP core.
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LedgerTransferEvent extends LedgerEvent {

    @JsonProperty("id")
    private final LedgerTransferId ledgerTransferId;
    // See https://github.com/interledger/rfcs/issues/137
    @JsonProperty("ilp_packet_header")
    private final Object ilpPacket;
    @JsonProperty("local_account_id")
    private final LedgerAccountId ledgerAccountId;
    @JsonProperty("local_transfer_amount")
    private final BigDecimal amount;

    /**
     * No-args Constructor.  Exists only for Jackson compatibility.
     */
    protected LedgerTransferEvent() {
        this.ledgerTransferId = null;
        this.ilpPacket = null;
        this.ledgerAccountId = null;
        this.amount = null;
    }

    public LedgerTransferEvent(
            final LedgerInfo source,
            final LedgerTransferId ledgerTransferId,
            final Object ilpPacket,
            final LedgerAccountId ledgerAccountId,
            final BigDecimal amount
    ) {
        super(source);

        this.ledgerTransferId = Objects.requireNonNull(ledgerTransferId);
        this.ilpPacket = ilpPacket;
        this.ledgerAccountId = Objects.requireNonNull(ledgerAccountId);
        this.amount = Objects.requireNonNull(amount);
    }

}
