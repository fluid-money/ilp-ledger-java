package org.ilpx.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerId;

/**
 * An ILP address that uniquely identifies an account in a ledger for purposes of ILP transactions.
 *
 * @deprecated Does a Ledger care what this value is?  There's a case that this should be ununsed in the ledgerAPI.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Deprecated
public class IlpAddress {

    private static final String ILP_ADDRESS_SEPARATOR = ".";

    // TODO: Does a Ledger care what this value is?  There's a case that this should be ununsed in the ledgerAPI.
    @NonNull
    private final String ilpAddress;

    /**
     * Helper method to create an instance of {@link IlpAddress}.
     */
    public static IlpAddress of(final LedgerId ledgerId, final LedgerAccountId accountId) {
        return new IlpAddress(accountId + ILP_ADDRESS_SEPARATOR + ledgerId);
    }

}
