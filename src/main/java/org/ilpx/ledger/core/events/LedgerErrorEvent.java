package org.ilpx.ledger.core.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;

@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LedgerErrorEvent extends LedgerEvent {

    private static final long serialVersionUID = -6494295568908151670L;

    protected final Exception error;

    public LedgerErrorEvent(LedgerInfo source, Exception error) {
        super(source);
        this.error = error;
    }

}
