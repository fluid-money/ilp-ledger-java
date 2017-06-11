package money.fluid.ilp.ledger.events.listener;

import money.fluid.ilp.ledger.model.LedgerInfo;
import org.ilpx.core.LedgerEventListener;
import org.ilpx.ledger.core.LedgerEventHandler;
import org.ilpx.ledger.core.events.LedgerEvent;


public class WebsocketClientEventListener implements LedgerEventListener {

    @Override
    public void registerEventHandler(LedgerEventHandler<?> handler) {

    }

    @Override
    public void unRegisterEventHandler(LedgerEventHandler<?> handler) {

    }

    @Override
    public void unRegisterEventHandlers() {

    }

    @Override
    public void notifyEventHandlers(LedgerEvent ledgerEvent) {

    }

    @Override
    public LedgerInfo getLedgerInfo() {
        return null;
    }
}
