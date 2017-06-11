package money.fluid.ilp.ledger.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * A class used for representing a Message resource.
 */
@Builder
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class MessageRepresentation {

    /**
     * The base URL of this ledger. MUST be an HTTP(S) URL where the client can Get Ledger Metadata.
     */
    @JsonProperty("ledger")
    private final String ledgerUrl;


    /**
     * Resource identifier of the account sending the message. MUST be an HTTP(S) URL where the client can get account
     * information. The sender of a message MUST be authenticated as the owner of this account.
     */
    @JsonProperty("from")
    private final String fromAccountUrl;

    /**
     * Resource identifier of the account receiving the message. MUST be an HTTP(S) URL where the client can get account
     * information.
     */
    @JsonProperty("to")
    private final String toAccountUrl;

    /**
     * The message to send, containing arbitrary data. A ledger MAY set a maximum length on messages, but that limit
     * MUST NOT be less than 510 UTF-8 characters or 2,048 bytes.
     */
    @JsonProperty("data")
    private final String data;

}
