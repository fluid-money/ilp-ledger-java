package money.fluid.ilp.ledger.utils;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import java.util.Currency;

/**
 *
 * @author mrmx
 */
public class CurrencyUtils {

    private CurrencyUtils() {
    }

    public static String getSymbol(String currencyCode) {
        return toCurrency(currencyCode).getSymbol();
    }

    public static Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }

    //FIXME
    public static int getPrecision(CurrencyUnit currency) {
        MonetaryAmountFactory<?> amountFactory = Monetary.getDefaultAmountFactory();
        MonetaryAmount amount = amountFactory.setCurrency(currency).setNumber(0).create();
        return amount.getNumber().getPrecision();
    }

}
