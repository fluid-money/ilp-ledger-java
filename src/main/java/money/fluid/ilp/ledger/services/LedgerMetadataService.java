package money.fluid.ilp.ledger.services;

import money.fluid.ilp.ledger.model.Metadata;

/**
 * A service for accessing metadata about this ledger.
 */
public interface LedgerMetadataService {
    /**
     * Get the metadata for this server, accounting for the signed-in user's security context.
     */
    Metadata getLedgerMetadata();
}
