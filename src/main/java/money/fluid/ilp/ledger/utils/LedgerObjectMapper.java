package money.fluid.ilp.ledger.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sappenin.utils.json.jackson.mappers.modules.HttpUrlModule;
import money.fluid.ilp.ledger.utils.jackson.BigDecimalAmountModule;
import org.zalando.jackson.datatype.money.MoneyModule;
import org.zalando.problem.ProblemModule;

import java.util.Objects;

/**
 * An extension of {@link ObjectMapper}.
 */
public class LedgerObjectMapper extends ObjectMapper {
    private static final long serialVersionUID = 1L;

    private final Integer ledgerPrecision;
    private final Integer ledgerScale;
    private final String roundingModeString;

    /**
     * Required-args Constructor.
     *
     * @param ledgerPrecision
     * @param ledgerScale
     * @param roundingModeString
     */
    public LedgerObjectMapper(final Integer ledgerPrecision, final Integer ledgerScale, String roundingModeString) {
        this.ledgerPrecision = Objects.requireNonNull(ledgerPrecision);
        this.ledgerScale = Objects.requireNonNull(ledgerScale);
        this.roundingModeString = Objects.requireNonNull(roundingModeString);

        registerModule(new HttpUrlModule());
        // Enables Joda Searialization/Deserialization
        // See https://github.com/FasterXML/jackson-datatype-joda
        registerModule(new JodaModule());
        registerModule(new GuavaModule());
        registerModule(new Jdk8Module());

        // For javax.money Searialization/Deserialization...
        registerModule(new MoneyModule());

        // For HttpUrl Searialization/Deserialization...
        registerModule(new HttpUrlModule());
        registerModule(new JavaTimeModule());
        registerModule(new BigDecimalAmountModule(ledgerPrecision, ledgerScale, roundingModeString));
        registerModule(new ProblemModule().withStackTraces(false));

        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        setDateFormat(new ISO8601DateFormat());

//        setVisibility(
//                getSerializationConfig().getDefaultVisibilityChecker()
//                        .withFieldVisibility(Visibility.ANY)
//                        .withGetterVisibility(Visibility.ANY)
//                        .withSetterVisibility(Visibility.NONE)
//                        .withCreatorVisibility(Visibility.PUBLIC_ONLY)
//                        .withIsGetterVisibility(Visibility.NONE)
//        );
    }

    @Override
    public ObjectMapper copy() {
        return new LedgerObjectMapper(ledgerPrecision, ledgerScale, roundingModeString);
    }

}
