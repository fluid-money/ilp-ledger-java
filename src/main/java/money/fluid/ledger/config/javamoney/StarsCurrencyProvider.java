package money.fluid.ledger.config.javamoney;

import org.javamoney.moneta.CurrencyUnitBuilder;

import javax.money.CurrencyQuery;
import javax.money.CurrencyUnit;
import javax.money.spi.CurrencyProviderSpi;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of {@link CurrencyProviderSpi} for registering a fictional currency called 'Stars' that is
 * denominated as 1/100th of a USD cent per single note.  The currency-code for this currency is "STRS".
 */
public class StarsCurrencyProvider implements CurrencyProviderSpi {

    public static final String STRS = "STRS";
    private Set<CurrencyUnit> sandSet = new HashSet<>();

    public StarsCurrencyProvider() {
        sandSet.add(
                CurrencyUnitBuilder.of(STRS, "StarsCurrencyBuilder")
                        .setDefaultFractionDigits(2)
                        .setCurrencyCode(STRS)
                        .build(true)
        );
        sandSet = Collections.unmodifiableSet(sandSet);
    }

    /**
     * Return a {@link CurrencyUnit} instances matching the given
     * {@link CurrencyQuery}.
     *
     * @param query the {@link CurrencyQuery} containing the parameters determining the query. not null.
     * @return the corresponding {@link CurrencyUnit}s matching, never null.
     */
    @Override
    public Set<CurrencyUnit> getCurrencies(CurrencyQuery query) {
        // only ensure STRS is the code, or it is a default query.
        if (query.isEmpty()
                || query.getCurrencyCodes().contains(STRS)
                || query.getCurrencyCodes().isEmpty()) {
            return sandSet;
        }
        return Collections.emptySet();
    }
}