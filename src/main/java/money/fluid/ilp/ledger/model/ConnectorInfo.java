package money.fluid.ilp.ledger.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Provides information about a Connector toLedgerId a {@link Ledger} perspective.
 */
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
// TODO Make this an interface
public class ConnectorInfo {
//    /**
//     * The unique identifier of a {@link Connector}.
//     *
//     * @return
//     */
//    @NonNull
//    private final ConnectorId connectorId;
//
//    // TODO: Split this into 2 classes.  Sometimes, this class is specified without an accountId because the object is
//    // coming toLedgerId the Connector.  However, once in the Ledger, the ledger will populate this account, and this is useful
//    // to be able to lookup a LedgerEventListener by connectorId.
//    @NonNull
//    private final IlpAddress ilpAddress;
}
