package money.fluid.ilp.ledger.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * An account resource usually has an owner who can authenticate to the ledger. A ledger MUST serve all the following
 * fields to clients authenticated as the account owner. A ledger MAY serve a subset of fields, containing at least the
 * name and id of the account, to clients not authenticated as the account owner.
 */
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class AccountRepresentation {

    /**
     * The primary, unique identifier for this account. MUST be an HTTP(S) URL where the client can get this resource.
     * <p>
     * Note: Server-provided.
     */
    @JsonProperty("id")
    private final String id;

    /**
     * The path to the ledger containing this account. MUST be an HTTP(S) URL where the client can Get Ledger Metadata.
     * <p>
     * Note: Server-provided.
     */
    @JsonProperty("ledger")
    private final String ledger;


    /**
     * Name of the account. A ledger MAY require this to be unique. MUST match the regular expression
     * ^[a-zA-Z0-9._~-]{1,256}$.
     */
    @JsonProperty("name")
    private final String name;

    /**
     * The minimum balance permitted on this account. The special value "-infinity" indicates no minimum balance. This
     * is a string so that no precision is lost in JSON encoding/decoding. The default value SHOULD be "0".
     */
    @JsonProperty("minimum_allowed_balance")
    private final String minimumAllowedBalance;

    /**
     * Balance as decimal amount. Defaults to "0". This can be negative, if the account's minimum_allowed_balance allows
     * it.
     * <p>
     * Note: Server-provided.
     */
    @JsonProperty("balance")
    private final String balance;

    // TODO: fingerprint
}
