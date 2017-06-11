package money.fluid.ilp.ledger.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A description of the resource tracked in this ledger, so that client applications can display appropriate information
 * to users.
 */
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class AssetRepresentation {

    /**
     * The type of asset. Currently, the only supported type is iso4217-currency.
     */
    @JsonProperty("type")
    private final String type;

    /**
     * The currency code to represent this asset. For iso4217-currency assets, this MUST be a three-letter uppercase ISO
     * 4217 currency code.
     */
    @JsonProperty("code")
    private final String code;

    /**
     * Symbol to use in user interfaces with amounts of this asset. For example, "$".
     */
    @JsonProperty("symbol")
    private final String symbol;

}
