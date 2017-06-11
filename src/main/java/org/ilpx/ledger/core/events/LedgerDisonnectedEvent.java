package org.ilpx.ledger.core.events;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;

/**
 * @deprecated Is this used in CLAPI?
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class LedgerDisonnectedEvent extends LedgerEvent {

    private static final long serialVersionUID = -2688034526014826323L;

    public LedgerDisonnectedEvent(final LedgerInfo source) {
        super(source);
    }

}
