package money.fluid.ilp.ledger.web.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerAccount;
import money.fluid.ilp.ledger.web.UrlService;
import money.fluid.ilp.ledger.web.model.AccountRepresentation;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import javax.money.MonetaryAmountFactory;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

/**
 * A factory for assembling instances of {@link AccountRepresentation} from various raw materials.
 */
@Component
@RequiredArgsConstructor
@ToString
public class AccountRepresentationFactory {

    //private final DefaultMonetaryAmountFormatFactory factory;
    //private final MonetaryAmountFormat monetaryAmountFormat;
    private final UrlService urlService;

    /**
     * Construct an instance of {@link AccountRepresentation} from an instance of {@link LedgerAccount}.
     *
     * @param ledgerAccount
     * @return
     */
    public AccountRepresentation construct(final LedgerAccount ledgerAccount) {
        Objects.requireNonNull(ledgerAccount);

        final AccountRepresentation.AccountRepresentationBuilder builder = AccountRepresentation.builder();

        builder.id(urlService.buildAccountUrl(ledgerAccount.getLedgerAccountId()).toString());
        builder.ledger(urlService.buildLedgerUrl().toString());
        builder.name(ledgerAccount.getLedgerAccountId().value());


        MonetaryAmountFactory<?> factory = Monetary.getDefaultAmountFactory();
        // MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.getDefault());

        // TODO Inject scale and precision from config properties...

//        final MonetaryContext context = MonetaryContextBuilder.of().setMaxScale(2).setPrecision(2).build();
//        final CurrencyUnit currencyUnit = null;
//        final MonetaryAmount formattedLedgerAmount = factory
//                //.setCurrency(currencyUnit)
//                .setContext(context)
//                .setNumber(ledgerAccount.getBalance().getNumber())
//                .create();

        //factory.

        // format the given amount
        //MonetaryAmount amount = factory.setCurrency(currencyUnit).setNumber(12.50).create();

       // final MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.getDefault(), new String[0]);
        //final String formatted = format.format(ledgerAccount.getBalance()); // result: CHF 12,50

        final String formattedAmount = ledgerAccount.getBalance().getNumber().numberValue(BigDecimal.class).toPlainString();

        // TODO: Need to create a custom formatter that either shows decimal like ###.##

        builder.balance(formattedAmount);
        builder.minimumAllowedBalance("0");
        // TODO Fingerprint

        return builder.build();
    }
}
