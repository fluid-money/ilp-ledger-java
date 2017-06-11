package money.fluid.ilp.ledger.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.squareup.okhttp.HttpUrl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * An extension of {@link SimpleModule} for registering with Jackson
 * {@link ObjectMapper} to serialize instances of {@link BigDecimal}.
 *
 * @see "http://wiki.fasterxml.com/JacksonMixInAnnotations"
 * @see "https://github.com/square/okhttp/blob/master/okhttp/src/main/java/com/squareup/okhttp/HttpUrl.java"
 */
public class BigDecimalAmountModule extends SimpleModule {

    /**
     * No-args Constructor.
     *
     * @param ledgerPrecision
     * @param ledgerScale
     * @param roundingModeString
     */
    public BigDecimalAmountModule(
            final Integer ledgerPrecision, final Integer ledgerScale, final String roundingModeString
    ) {
        super("MonetaryAmountModule", new Version(1, 0, 0, null));
        this.addSerializer(
                BigDecimal.class, new BigDecimalStringSerializer(ledgerPrecision, ledgerScale, roundingModeString));
        this.addDeserializer(
                BigDecimal.class, new BigDecimalStringDeserializer(ledgerPrecision, ledgerScale, roundingModeString));
    }

    /**
     * An extension of {@link FromStringDeserializer} that deserializes a JSON
     * string into an instance of {@link HttpUrl}.
     */
    public static class BigDecimalStringSerializer extends ToStringSerializer {

        private final Integer ledgerPrecision;

        private final Integer ledgerScale;

        private final String roundingModeString;

        /**
         * No-args Constructor.
         *
         * @param ledgerPrecision
         * @param ledgerScale
         * @param roundingModeString
         */
        public BigDecimalStringSerializer(
                final Integer ledgerPrecision, final Integer ledgerScale, final String roundingModeString
        ) {
            super(BigDecimal.class);
            this.ledgerPrecision = Objects.requireNonNull(ledgerPrecision);
            this.ledgerScale = Objects.requireNonNull(ledgerScale);
            this.roundingModeString = Objects.requireNonNull(roundingModeString);
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            final BigDecimal scaledValue = scale(
                    ((BigDecimal) value), ledgerPrecision, ledgerScale, roundingModeString);

            gen.writeString(scaledValue.toEngineeringString());
            //gen.writeString(scaledValue.toString());
        }
    }

    /**
     * An extension of {@link FromStringDeserializer} that deserializes a JSON
     * string into an instance of {@link HttpUrl}.
     */
    public static class BigDecimalStringDeserializer extends FromStringDeserializer<BigDecimal> {

        private final Integer ledgerPrecision;

        private final Integer ledgerScale;

        private final String roundingModeString;

        /**
         * No-args Constructor.
         *
         * @param ledgerPrecision
         * @param ledgerScale
         * @param roundingModeString
         */
        public BigDecimalStringDeserializer(
                final Integer ledgerPrecision, final Integer ledgerScale, final String roundingModeString
        ) {
            super(BigDecimal.class);
            this.ledgerPrecision = Objects.requireNonNull(ledgerPrecision);
            this.ledgerScale = Objects.requireNonNull(ledgerScale);
            this.roundingModeString = Objects.requireNonNull(roundingModeString);
        }

        @Override
        protected BigDecimal _deserialize(
                final String s, final DeserializationContext deserializationContext
        ) throws IOException {
            return scale(new BigDecimal(s), ledgerPrecision, ledgerScale, roundingModeString);
        }
    }

    /**
     * Adjust the precision and scale of a supplied {@link BigDecimal} to match the values specified by this ledger.
     *
     * @param input An instance of {@link BigDecimal} to adjust.
     * @return
     * @see
     */
    public static BigDecimal scale(
            final BigDecimal input, final Integer newPrecision, final Integer newScale, final String roundingModeString
    ) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(newPrecision);
        Objects.requireNonNull(newScale);
        Objects.requireNonNull(roundingModeString);

        final RoundingMode roundingMode = toRoundingMode(roundingModeString);

        // See "http://stackoverflow.com/questions/9482889/set-specific-precision-of-a-bigdecimal", but I think the
        // poster is incorrect.  Increasing the precision of "1.00" to 7 should probably yield a precision of 3.
        return input.setScale(newScale, roundingMode).round(new MathContext(newPrecision, roundingMode));

//
//        if (newScale.intValue() < 0) {
//            input.setScale(0);
//        } else {
//            input.setScale(newScale, roundingMode);
//        }
//
//        // Unlimited Precision!
//        if (newPrecision.intValue() > 0) {
//            return input.setScale(input.scale() + newPrecision - input.precision(), roundingMode);
//        } else {
//            return input;
//        }
    }

    private static RoundingMode toRoundingMode(final String roundingModeString) {
        switch (roundingModeString.toLowerCase()) {
            case "floor":
                return RoundingMode.FLOOR;
            case "ceil":
                return RoundingMode.CEILING;
            case "nearest":
                return RoundingMode.HALF_UP;
            default:
                throw new RuntimeException("Unhandled roundingModeString: " + roundingModeString);
        }
    }
}
