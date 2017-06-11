package money.fluid.ilp.ledger.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.squareup.okhttp.HttpUrl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Status;
import money.fluid.ilp.ledger.model.Transfer.Timeline;
import org.interledger.cryptoconditions.Condition;

import java.math.BigDecimal;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * An external representation of a transferFunds, which represents money being moved around within a single ledger. A
 * transferFunds debits one account and credits another account for the exact same amount.
 * <p>
 * A transferFunds can be conditional upon a supplied Crypto-Condition, in which case it executes automatically when
 * presented with the fulfillment for the condition. (Assuming the transferFunds has not expired or been canceled first.) If
 * no crypto-condition is specified, the transferFunds is unconditional, and executes as soon as it is prepared.
 */
@Getter
@EqualsAndHashCode
@ToString
@JsonPropertyOrder({
        "id", "debit_account", "credit_account", "amount", "execution_condition", "expires_at", "status",
        "timelineRepresentation"
})
public class TransferRepresentation {

    /**
     * A unique identifier for this {@link Transfer}.
     */
    @JsonProperty("id")
    private final String id;

    /**
     * The {@link HttpUrl} of the account debited by this transferFunds.
     */
    @JsonProperty("debit_account")
    private final Optional<URI> debitAccount;

    /**
     * The {@link HttpUrl} of the account credited by this transferFunds.
     */
    @JsonProperty("credit_account")
    private final Optional<URI> creditAccount;

    /**
     * The amount of the currency/asset being transferred. This is debited from {@link #getDebitAccount()} and
     * credited to  {@link #getCreditAccount()} when this transferFunds executes.
     */
    @JsonProperty("amount")
    private final Optional<BigDecimal> amount;

    /**
     * The status of this transferFunds, as a {@link String}.  Corresponds to {@link Status}.
     */
    @JsonProperty("status")
    private final String status;

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
     * The {@link Timeline} of this transferFunds's state transitions.
     */
    @JsonProperty("timeline")
    private final TimelineRepresentation timelineRepresentation;

    /**
     * Arbitrary data provided by {@link #getDebitAccount()} for {@link #getCreditAccount()}. Also, the connector stores
     * the ILP Header in this field.
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
     * Arbitrary data provided by the {@link #getDebitAccount()} for itself.  This can be encoded on the wire in
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

    public TransferRepresentation(final Builder builder) {
        Objects.requireNonNull(builder);

        this.id = builder.id;
        this.debitAccount = builder.debitAccount;
        this.creditAccount = builder.creditAccount;
        this.amount = builder.amount;
        this.status = builder.status;
        this.timelineRepresentation = builder.timeline;
        this.executionCondition = builder.executionCondition;
        this.expiresAt = builder.expiresAt;
        this.memo = builder.getMemo();
        this.noteToSelf = builder.getNoteToSelf();
        this.additionalInfo = builder.getAdditionalInfo();
    }

    public static TransferRepresentation.Builder builder() {
        return new TransferRepresentation.Builder();
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Builder {
        private String id;

        private Optional<URI> debitAccount;

        private Optional<URI> creditAccount;

        private Optional<BigDecimal> amount;

        private String status;

        private TimelineRepresentation timeline;

        private Optional<Condition> executionCondition;

        private Optional<ZonedDateTime> expiresAt;

        private Optional<String> memo;

        private Optional<String> noteToSelf;

        private Optional<String> additionalInfo;

        public Builder() {
            executionCondition = Optional.empty();
            expiresAt = Optional.empty();
            memo = Optional.empty();
            noteToSelf = Optional.empty();
            additionalInfo = Optional.empty();
        }

        public TransferRepresentation build() {
            return new TransferRepresentation(this);
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder debitAccount(final URI debitAccount) {
            this.debitAccount = Optional.of(debitAccount);
            return this;
        }

        public Builder creditAccount(final URI creditAccount) {
            this.creditAccount = Optional.of(creditAccount);
            return this;
        }

        public Builder amount(final BigDecimal amount) {
            this.amount = Optional.of(amount);
            return this;
        }

        public Builder status(final String status) {
            this.status = status;
            return this;
        }

        public Builder timeline(final TimelineRepresentation timelineRepresentation) {
            this.timeline = timelineRepresentation;
            return this;
        }

        public Builder executionCondition(final Optional<Condition> executionCondition) {
            this.executionCondition = executionCondition;
            return this;
        }

        public Builder expiresAt(final Optional<ZonedDateTime> expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder memo(final Optional<String> memo) {
            this.memo = memo;
            return this;
        }

        public Builder noteToSelf(final Optional<String> noteToSelf) {
            this.noteToSelf = noteToSelf;
            return this;
        }

        public Builder additionalInfo(final Optional<String> additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }
    }

    /**
     * Timeline of a transferFunds's state transitions.
     */
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class TimelineRepresentation {

        /**
         * The {@link ZonedDateTime} when the transferFunds was originally prepared.
         *
         * @return An instance of {@link ZonedDateTime}.
         */
        @NonNull
        @JsonProperty("created_at")
        private final ZonedDateTime createdAt;

        /**
         * The {@link } when the transferFunds was originally executed. This value MUST only appear if {@link
         * Transfer#getStatus} is "EXECUTED". This time MUST be equal to or later than
         * {@link #getCreatedAt}.
         *
         * @return An instance of {@link }.
         */
        @NonNull
        @JsonProperty("executed_at")
        private final Optional<ZonedDateTime> executedAt;

        /**
         * The {@link }  when the transferFunds was originally rejected.  MUST appear if and only if {@link
         * Transfer#getStatus()} is "REJECTED". This time MUST be equal to or later than
         * {@link #getCreatedAt}.
         */
        @NonNull
        @JsonProperty("rejected_at")
        private final Optional<ZonedDateTime> rejectedAt;

        public TimelineRepresentation(final Builder builder) {
            Objects.requireNonNull(builder);

            this.createdAt = builder.createdAt;
            this.executedAt = builder.executedAt;
            this.rejectedAt = builder.rejectedAt;
        }

        public static Builder builder(final ZonedDateTime createdAt) {
            return new Builder(createdAt);
        }

        @ToString
        @EqualsAndHashCode
        public static class Builder {

            private final ZonedDateTime createdAt;

            private Optional<ZonedDateTime> executedAt;

            private Optional<ZonedDateTime> rejectedAt;

            public Builder(final ZonedDateTime createdAt) {
                this.createdAt = createdAt;
                this.executedAt = Optional.empty();
                this.rejectedAt = Optional.empty();
            }

            public TimelineRepresentation build() {
                return new TimelineRepresentation(this);
            }

            public Builder executedAt(Optional<ZonedDateTime> executedAt) {
                this.executedAt = executedAt;
                return this;
            }

            public Builder rejectedAt(Optional<ZonedDateTime> rejectedAt) {
                this.rejectedAt = rejectedAt;
                return this;
            }
        }
    }
}
