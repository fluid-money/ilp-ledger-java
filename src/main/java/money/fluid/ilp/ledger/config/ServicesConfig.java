package money.fluid.ilp.ledger.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.Metadata;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import money.fluid.ilp.ledger.utils.LedgerObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.inject.Inject;
import javax.money.CurrencyContext;
import javax.money.CurrencyContextBuilder;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmountFactory;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.Executors;

@Configuration
@ComponentScan(basePackages = {
        "money.fluid.ilp.ledger.services",
        "money.fluid.ilp.ledger.utils",
        "money.fluid.ilp.ledger.web",
        "money.fluid.ilp.ledger.events",
        "money.fluid.ledger.services",
})
public class ServicesConfig {

    @Bean
    public ObjectMapper objectMapper(
            @Value("${ledger.precision:0}") final Integer ledgerPrecision,
            @Value("${ledger.scale:2}") final Integer ledgerScale,
            @Value("${ledger.rounding:NEAREST}") final String roundingModeString
    ) {
        return new LedgerObjectMapper(ledgerPrecision, ledgerScale, roundingModeString);
    }

    @Bean
    @Inject
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
            final ObjectMapper ledgerObjectMapper
    ) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        //ObjectMapper objectMapper = new ObjectMapper();
        ledgerObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonConverter.setObjectMapper(ledgerObjectMapper);
        return jsonConverter;
    }

    @Value("${ledger.asset.currency_provider.spi}")
    private String currencyProviderSpiName;

    @Value("${ledger.asset.code}")
    private String ledgerAssetCode;

    @Bean
    public CurrencyUnit currencyUnit() {
        // TODO: Make these values arrays so that multiple codes/provides can be registered?  Or more likely, specify these in an augment property file, and then pass in a location via environment?
        // Register a currency unit provider, but only if the ledger asset code and SPI are specified.
        if (StringUtils.isBlank(currencyProviderSpiName) == false && StringUtils.isBlank(ledgerAssetCode) == false) {
            final CurrencyContext currencyContext = CurrencyContextBuilder.of(currencyProviderSpiName).build();
            // Just register this provider programatically...
            return CurrencyUnitBuilder.of(ledgerAssetCode, currencyContext).build(true);
        } else {
            // By default, return USD...
            return Money.of(BigDecimal.ZERO, "USD").getCurrency();
        }
    }

    @Bean
    @Inject
    public LedgerInfo getLedgerInfo(
            final CurrencyUnit currencyUnit, final LedgerMetadataService ledgerMetadataService
    ) {
        final Metadata ledgerMetadata = ledgerMetadataService.getLedgerMetadata();
        final LedgerId ledgerId = ledgerMetadata.getIlpPrefix();
        return new LedgerInfo(
                ledgerMetadata.getAssetInfo().getPrecision(),
                ledgerMetadata.getAssetInfo().getScale(),
                currencyUnit.getCurrencyCode(),
                ledgerId
        );
    }

    /**
     * An Async {@link EventBus} from Guava.
     */
    @Bean
    public EventBus eventBus() {
        return new AsyncEventBus(Executors.newCachedThreadPool());
    }


    // TODO: Use JavaMoney ExchangeRateProvider.  See https://dzone.com/articles/looking-java-9-money-and
//    @Bean
//    ExchangeRateProvider exchangeRateProvider() {
//        return MonetaryConversions.getExchangeRateProvider();
//    }


    @Bean
    public MonetaryAmountFormat monetaryAmountFormat(final CurrencyUnit currencyUnit) {

        MonetaryAmountFactory<?> f = Monetary.getDefaultAmountFactory();
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.getDefault());
        return format;
        // format the given amount
        //MonetaryAmount amount = f.setCurrency(currencyUnit).setNumber(12.50).create();
        // String formatted = format.format(amount); // result: CHF 12,50


    }

}