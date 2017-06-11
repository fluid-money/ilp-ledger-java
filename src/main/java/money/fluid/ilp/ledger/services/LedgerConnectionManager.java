package money.fluid.ilp.ledger.services;

import money.fluid.ilp.ledger.model.ConnectionInfo;
import org.ilpx.core.IlpAddress;
import org.ilpx.ledger.core.Ledger;
import org.ilpx.ledger.core.LedgerEventHandler;
import org.ilpx.ledger.core.events.LedgerEvent;

/**
 * An interface that manages how outside processes can connect to a Ledger.
 * <p>
 * Examples of outside processed include a Connector, Connector-plugin, browser, etc.
 */
public interface LedgerConnectionManager {
    void connect(ConnectionInfo connectionInfo);

    void disconnect(IlpAddress ilpAddress);

    /**
     * Registers a {@link LedgerEventHandler} with a {@link Ledger}, associated to a particular connector.
     *
     * @param ilpAddress         A unique client id.
     * @param ledgerEventHandler
     */
    void registerEventHandler(IlpAddress ilpAddress, LedgerEventHandler ledgerEventHandler);

    void notifyEventListeners(IlpAddress ilpAddress, LedgerEvent ledgerEvent);
}
