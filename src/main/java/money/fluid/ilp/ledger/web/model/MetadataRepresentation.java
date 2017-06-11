package money.fluid.ilp.ledger.web.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.ilpx.ledger.core.Ledger;

/**
 * Meta-data about a {@link Ledger}.
 */
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MetadataRepresentation {

    /**
     * Information about the currency or other asset that a {@link Ledger} tracks.
     */
    @JsonProperty("asset_info")
    @NonNull
    private final AssetRepresentation assetInfo;

    /**
     * The ILP Address prefix of the ledger.
     */
    @JsonProperty("ilp_prefix")
    @NonNull
    private final String ilpPrefix;

    /**
     * Get the precision for this {@link Ledger}, which is how many total decimal digits this ledger uses to represent
     * currency amounts.  A value of 0 indicated unlimited precision, which is the default value for this method.
     */
    @JsonProperty("precision")
    @NonNull
    private final Integer precision;

    /**
     * Get the scale for this {@link Ledger}, which is how many digits after the decimal place this ledger supports in
     * currency amounts.  This method defaults to 2.
     */
    @JsonProperty("scale")
    @NonNull
    private final Integer scale;

    /**
     * Get a {@link String} representation of the rounding used internally by ledger for values that exceed the reported
     * scale or precision.
     */
    @JsonProperty("rounding")
    @NonNull
    private final String rounding;
}
