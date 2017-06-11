package money.fluid.ilp.ledger.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerId;

import javax.money.CurrencyUnit;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class LedgerInfo {

    /**
     * Get the precision supported by the ledger
     */
    @NonNull
    private final int precision;

    /**
     * Get the scale allowed by the ledger
     */
    @NonNull
    private final int scale;

    /**
     * Get the ISO-4217 currency code of the ledger.
     */
    @NonNull
    private final String currencyUnit;

    /**
     * The Ledger identifier for this ledger.
     */
    @NonNull
    private final LedgerId ledgerId;

}