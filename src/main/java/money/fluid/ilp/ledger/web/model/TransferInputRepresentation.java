package money.fluid.ilp.ledger.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.services.transfer.TransferValidator;
import org.interledger.cryptoconditions.Condition;

import java.math.BigDecimal;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * A transferFunds represents money being moved around within a single ledger. A transferFunds debits one account and credits
 * another account for the exact same amount.
 * <p>
 * A transferFunds can be conditional upon a supplied Crypto-Condition, in which case it executes automatically when
 * presented with the fulfillment for the condition. (Assuming the transferFunds has not expired or been canceled first.) If
 * no crypto-condition is specified, the transferFunds is unconditional, and executes as soon as it is prepared.
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class TransferInputRepresentation {

    /**
     * An optionally-supplied unique identifier for this client.  See the requirements for this field in {@link
     * TransferValidator}.  This field exists for convenience so that clients who wish to specify the identifier of a
     * transferFunds in a single call may do so.
     */
    @JsonProperty("client_id")
    private final Optional<String> clientId;

    /**
     * An optionally-supplied {@link String} that, if present, can be "PROPOSED" or "CREATED".
     */
    @JsonProperty("status")
    private final Optional<String> status;

    /**
     * The {@link URI} of the account debited by this transferFunds.
     */
    @JsonProperty("debit_account")
    private final Optional<URI> debitAccountId;

    /**
     * The {@link URI} of the account credited by this transferFunds.
     */
    @JsonProperty("credit_account")
    private final Optional<URI> creditAccountId;

    /**
     * The amount of the currency/asset being transferred. This is debited toLedgerId {@link #getDebitAccountId()} and
     * credited to an account determined by the Ledger.
     */
    @JsonProperty("amount")
    private final Optional<BigDecimal> amount;

    /**
     * The condition for executing the transferFunds. If omitted, the transferFunds executes unconditionally.
     */
    @JsonProperty("execution_condition")
    private final Optional<Condition> executionCondition;

    /**
     * The date when the transferFunds expires and can no longer be executed.
     */
    @JsonProperty("expires_at")
    private final Optional<ZonedDateTime> expiresAt;

    /**
     * Arbitrary data provided by {@link #getDebitAccountId()} for the account that will be credited by this transferFunds.
     * Also, the connector stores the ILP Header in this field.
     * <p>
     * Ledgers SHOULD treat this data as opaque, however it will usually start with an ILP header followed by a
     * transport layer header, a quote request or a custom user-provided data packet.
     * <p>
     * If the data is too large, the ledger MUST throw a MaximumDataSizeExceededError. If the data is too large only
     * because the amount is insufficient, the ledger plugin MUST throw an InsufficientAmountError.
     */
    @JsonProperty("memo")
    private final Optional<String> memo;

    /**
     * Arbitrary data provided by the {@link #getDebitAccountId()} for itself.  This can be encoded on the wire in
     * any format chosen by an implementation, while being treated as a typed object in the JVM.  For example, this
     * could be an optional bytestring containing details the host needs to persist with the transferFunds in order to be
     * able to react to transferFunds events like condition fulfillment later.
     * <p>
     * NOTE: This field MUST be hidden when not authenticated as the debit_account or an admin.  In other words, this
     * data MUST NOT be shared with any untrusted party or it should be encrypted before being shared outside of the
     * Leger.
     * <p>
     * NOTE: Ledgers MUST ensure that all instances of the transferFunds carry the same noteToSelf, even across different
     * machines.
     */
    @JsonProperty("note_to_self")
    private final Optional<String> noteToSelf;

    /**
     * Arbitrary fields attached to this transferFunds. For example, the IDs of related transfers in other systems.
     */
    @JsonProperty("additional_info")
    private final Optional<String> additionalInfo;

    public static TransferInputRepresentation.Builder builder() {
        return new TransferInputRepresentation.Builder();
    }

    private TransferInputRepresentation(Builder builder) {
        Objects.requireNonNull(builder);

        this.clientId = builder.clientId;
        this.status = builder.status;
        this.debitAccountId = builder.debitAccountUri;
        this.creditAccountId = builder.creditAccountUri;
        this.amount = builder.amount;
        this.executionCondition = builder.executionCondition;
        this.expiresAt = builder.expiresAt;
        this.memo = builder.memo;
        this.noteToSelf = builder.noteToSelf;
        this.additionalInfo = builder.additionalInfo;
    }


    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Builder {

        private Optional<String> clientId;

        private Optional<String> status;

        private Optional<URI> debitAccountUri;

        private Optional<URI> creditAccountUri;

        private Optional<BigDecimal> amount;

        private Optional<Condition> executionCondition;

        private Optional<ZonedDateTime> expiresAt;

        private Optional<String> memo;

        private Optional<String> noteToSelf;

        private Optional<String> additionalInfo;

        public Builder() {
            clientId = Optional.empty();
            debitAccountUri = Optional.empty();
            creditAccountUri = Optional.empty();
            amount = Optional.empty();
            status = Optional.empty();
            executionCondition = Optional.empty();
            expiresAt = Optional.empty();
            memo = Optional.empty();
            noteToSelf = Optional.empty();
            additionalInfo = Optional.empty();
        }

        public TransferInputRepresentation build() {
            return new TransferInputRepresentation(this);
        }

        public Builder clientId(String clientId) {
            this.clientId = Optional.of(clientId);
            return this;
        }

        public Builder status(String status) {
            this.status = Optional.of(status);
            return this;
        }

        public Builder debitAccountUri(URI debitAccountURI) {
            this.debitAccountUri = Optional.of(debitAccountURI);
            return this;
        }

        public Builder creditAccountUri(URI creditAccountURI) {
            this.creditAccountUri = Optional.of(creditAccountURI);
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = Optional.of(amount);
            return this;
        }

        public Builder executionCondition(
                Condition executionCondition
        ) {
            this.executionCondition = Optional.of(executionCondition);
            return this;
        }

        public Builder expiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = Optional.of(expiresAt);
            return this;
        }

        public Builder memo(String memo) {
            this.memo = Optional.of(memo);
            return this;
        }

        public Builder noteToSelf(String noteToSelf) {
            this.noteToSelf = Optional.of(noteToSelf);
            return this;
        }

        public Builder additionalInfo(String additionalInfo) {
            this.additionalInfo = Optional.of(additionalInfo);
            return this;
        }
    }
}
