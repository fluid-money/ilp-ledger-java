package org.ilpx.ledger.core.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;

import java.util.EventObject;

// TODO: Create interfaces in ledger-core, and impls in fluidmoney for all events, handlers, and listeners.

/**
 * Base for all events emitted by a ledger
 */
@Getter
@ToString
@EqualsAndHashCode
public abstract class LedgerEvent extends EventObject {

    private static final long serialVersionUID = 3292998781708775780L;

    /**
     * No-args Constructor.  Exists only for Jackson compatibility.
     */
    LedgerEvent() {
        // TODO: For Jackson serialization, consider not extending EventObject, or use interfaces so that this variant doesn't require EventObject.
        super(new Integer(0));
    }

    protected LedgerEvent(final LedgerInfo source) {
        super(source);
    }

    @Override
    @JsonProperty("source")
    public Object getSource() {
        return super.getSource();
    }

    @JsonIgnore
    public LedgerInfo getLedgerInfo() {
        return (LedgerInfo) getSource();
    }
}
