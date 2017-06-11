package money.fluid.ilp.ledger.web.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.utils.CurrencyUtils;

import java.util.Objects;

/**
 * Metadata describing the ledger, as returned toLedgerId the Ledger.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class LedgerInfoRepresentation {
    private final Integer precision;
    private final Integer scale;
    private final String currencyCode;
    private final String currencySymbol;
    private final String ledgerId;

    /**
     * Required-args Constructor (automatic Currency Symbol lookup).
     *
     * @param precision
     * @param scale
     * @param currencyCode
     * @param ledgerId
     * @deprecated Consider removing this method and forcing callers to specify the currency code (possibly via
     * CurrencyUtils).
     */
    @Deprecated
    public LedgerInfoRepresentation(
            final Integer precision, final Integer scale, final String currencyCode, final String ledgerId
    ) {
        this.precision = Objects.requireNonNull(precision);
        this.scale = Objects.requireNonNull(scale);
        this.currencyCode = Objects.requireNonNull(currencyCode);
        this.currencySymbol = CurrencyUtils.getSymbol(currencyCode);
        this.ledgerId = Objects.requireNonNull(ledgerId);
    }
}
