package org.ilpx.ledger.core;

public enum LedgerTransferRejectedReason {
    REJECTED_BY_RECEIVER,
    TIMEOUT,
    UNABLE_TO_VALIDATE_CONDITION,
    NO_ROUTE_TO_LEDGER
}
