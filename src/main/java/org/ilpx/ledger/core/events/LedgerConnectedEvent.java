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
public class LedgerConnectedEvent extends LedgerEvent {

    private static final long serialVersionUID = 6501842605798174441L;

    public LedgerConnectedEvent(LedgerInfo source) {
        super(source);
    }
}
