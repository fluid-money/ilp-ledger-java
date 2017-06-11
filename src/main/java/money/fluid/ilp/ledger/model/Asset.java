package money.fluid.ilp.ledger.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Metadata.Rounding;
import org.ilpx.ledger.core.Ledger;

/**
 * A description of the resource tracked in this ledger, so that client applications can display appropriate information
 * to users.
 */
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Asset {

    /**
     * The type of asset. Currently, the only supported type is iso4217-currency.
     */
    private final String type;

    /**
     * The currency code to represent this asset. For iso4217-currency assets, this MUST be a three-letter uppercase ISO
     * 4217 currency code.
     */
    private final String code;

    /**
     * Symbol to use in user interfaces with amounts of this asset. For example, "$".
     */
    private final String symbol;

    /**
     * Get the precision for this {@link Ledger}, which is how many total decimal digits this ledger uses to represent
     * currency amounts.  A value of 0 indicated unlimited precision, which is the default value for this method.
     */
    private final int precision;

    /**
     * Get the scale for this {@link Ledger}, which is how many digits after the decimal place this ledger supports in
     * currency amounts.  This method defaults to 2.
     */
    private final int scale;

    /**
     * Get the {@link Rounding} used internally by ledger for values that exceed the reported scale or precision.
     */
    private final Rounding rounding;

}
