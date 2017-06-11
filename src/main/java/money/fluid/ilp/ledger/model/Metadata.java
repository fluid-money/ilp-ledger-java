package money.fluid.ilp.ledger.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import org.ilpx.ledger.core.Ledger;

/**
 * Meta-data about a {@link Ledger}.
 */
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Metadata {

    /**
     * Information about the currency or other asset that a {@link Ledger} tracks.
     */
    private final Asset assetInfo;

    /**
     * The ILP Address prefix of the ledger.
     */
    // TODO: Is this correct?  Should probably just be a String?
    private final LedgerId ilpPrefix;

    /**
     * The type of rounding used internally by ledger for values that exceed the reported scale or precision.
     */
    public enum Rounding {
        FLOOR, CEILING, NEAREST;
    }

    @RequiredArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Urls {
        @JsonProperty("transfers")
        private final String transfers;

        @JsonProperty("websocket")
        private final String websocket;

        @JsonProperty("transferFunds")
        private final String transfer;

        @JsonProperty("transfer_rejection")
        private final String transferRejection;

        @JsonProperty("transfer_fulfillment")
        private final String transferFulfillment;

        @JsonProperty("account")
        private final String account;
    }
}
