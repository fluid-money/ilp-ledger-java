package money.fluid.ilp.ledger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.ilpx.core.IlpAddress;
import org.interledger.cryptoconditions.Condition;

import javax.money.MonetaryAmount;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * A transferFunds represents money being moved around within a single ledger. A transferFunds debits one account and credits
 * another account for the exact same amount.
 * <p>
 * A transferFunds can be conditional upon a supplied Crypto-Condition, in which case it executes automatically when
 * presented with the fulfillment for the condition. (Assuming the transferFunds has not expired or been canceled first.) If
 * no crypto-condition is specified, the transferFunds is unconditional, and executes as soon as it is prepared.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Transfer {

    /**
     * A unique identifier for this {@link Transfer}.
     */
    private final LedgerTransferId id;

    /**
     * The {@link IlpAddress} of the account debited by this transferFunds.
     */
    private final Optional<LedgerAccountId> debitAccountId;

    /**
     * The {@link IlpAddress} of the account credited by this transferFunds.
     */
    private final Optional<LedgerAccountId> creditAccountId;

    /**
     * The amount of the currency/asset being transferred.
     */
    private final Optional<MonetaryAmount> amount;

    /**
     * The {@link Status} of this transferFunds.
     */
    private final Status status;

    /**
     * The condition for executing the transferFunds. If omitted, the transferFunds executes unconditionally.
     */
    private final Optional<Condition> executionCondition;

    /**
     * The date when the transferFunds expires and can no longer be executed.
     */
    private final Optional<ZonedDateTime> expiresAt;

    /**
     * The {@link Timeline} of this transferFunds's state transitions.
     */
    private final Transfer.Timeline timeline;

    /**
     * Arbitrary data provided by the debitAccountId for the creditAccountUri. Also, the connector stores the ILP Header
     * in this field.
     * <p>
     * Ledgers SHOULD treat this data as opaque, however it will usually start with an ILP header followed by a
     * transport layer header, a quote request or a custom user-provided data packet.
     * <p>
     * If the data is too large, the ledger MUST throw a MaximumDataSizeExceededError. If the data is too large only
     * because the amount is insufficient, the ledger plugin MUST throw an InsufficientAmountError.
     */
    private final Optional<String> memo;

    /**
     * Arbitrary data provided by the debite account for itself.  This can be encoded on the wire in any format chosen
     * by an implementation, while being treated as a typed object in the JVM.  For example, this could be an optional
     * bytestring containing details the host needs to persist with the transferFunds in order to be able to react to
     * transferFunds events like condition fulfillment later.
     * <p>
     * NOTE: This field MUST be hidden when not authenticated as the debit_account or an admin.  In other words, this
     * data MUST NOT be shared with any untrusted party or it should be encrypted before being shared outside of the
     * Leger.
     * <p>
     * NOTE: Ledgers MUST ensure that all instances of the transferFunds carry the same noteToSelf, even across different
     * machines.
     */
    private final Optional<String> noteToSelf;

    /**
     * Arbitrary fields attached to this transferFunds. For example, the IDs of related transfers in other systems.
     */
    private final Optional<String> additionalInfo;

    /**
     * Determine if this Transfer is an optimistic-mode transferFunds.
     *
     * @return {@code true} if both the {@code executionCondition} and {@code expiresAt} objects are both absent; {@code
     * false} otherwise.
     */
    public boolean isOptimisticMode() {
        return this.executionCondition.isPresent() == false && this.expiresAt.isPresent() == false;
    }

    public Transfer(final Transfer.Builder builder) {
        requireNonNull(builder);

        this.id = builder.id;
        this.debitAccountId = builder.debitAccountId;
        this.creditAccountId = builder.creditAccountId;
        this.amount = builder.amount;
        this.status = builder.status;
        this.timeline = builder.timeline;
        this.executionCondition = builder.executionCondition;
        this.expiresAt = builder.expiresAt;
        this.memo = builder.getMemo();
        this.noteToSelf = builder.getNoteToSelf();
        this.additionalInfo = builder.getAdditionalInfo();
    }

//    /**
//     * Get the packet header for this transferFunds, based upon all information contained in the Transfer.
//     *
//     * @return the Interledger Packet Header
//     */
//    private final InterledgerPacketHeader getInterledgerPacketHeader();

    public static Transfer.Builder builder() {
        return new Transfer.Builder();
    }

    /**
     * Valid ledger transferFunds statuses.
     */
    public enum Status {
        PREPARED,
        EXECUTED,
        REJECTED;

        /**
         * Helper to get the typed version of this transferFunds's status.
         */
        public static Optional<Status> valueOfQuiet(final String status) {
            try {
                return Optional.of(Status.valueOf(status));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    /**
     * A reason for rejecting a Transfer.
     */
    public enum RejectionReason {
        REJECTED_BY_RECEIVER,
        TIMEOUT,
        UNABLE_TO_VALIDATE_CONDITION,
        NO_ROUTE_TO_LEDGER
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Builder {
        private LedgerTransferId id;

        private Optional<LedgerAccountId> debitAccountId;

        private Optional<LedgerAccountId> creditAccountId;

        private Optional<MonetaryAmount> amount;

        private Transfer.Status status;

        private Transfer.Timeline timeline;

        private Optional<Condition> executionCondition;

        private Optional<ZonedDateTime> expiresAt;

        private Optional<String> memo;

        private Optional<String> noteToSelf;

        private Optional<String> additionalInfo;

        public Builder() {
            debitAccountId = Optional.empty();
            creditAccountId = Optional.empty();
            status = Status.PREPARED;
            amount = Optional.empty();
            executionCondition = Optional.empty();
            expiresAt = Optional.empty();
            memo = Optional.empty();
            noteToSelf = Optional.empty();
            additionalInfo = Optional.empty();
        }

        /**
         * Copy Constructor.
         *
         * @param transfer A pre-existing instance of {@link Transfer}.
         */
        public Builder(final Transfer transfer) {
            requireNonNull(transfer);

            this.id = transfer.getId();
            this.debitAccountId = transfer.getDebitAccountId();
            this.creditAccountId = transfer.getCreditAccountId();
            this.amount = transfer.getAmount();
            this.status = transfer.getStatus();
            this.timeline = transfer.getTimeline();
            this.executionCondition = transfer.getExecutionCondition();
            this.expiresAt = transfer.getExpiresAt();
            this.memo = transfer.getMemo();
            this.noteToSelf = transfer.getNoteToSelf();
            this.additionalInfo = transfer.getAdditionalInfo();
        }

        public Transfer build() {
            return new Transfer(this);
        }

        public Transfer.Builder id(LedgerTransferId id) {
            this.id = id;
            return this;
        }

        public Transfer.Builder debitAccountId(final Optional<LedgerAccountId> debitAccountId) {
            this.debitAccountId = debitAccountId;
            return this;
        }

        public Transfer.Builder creditAccountId(final Optional<LedgerAccountId> creditAccountId) {
            this.creditAccountId = creditAccountId;
            return this;
        }

        public Transfer.Builder amount(final MonetaryAmount amount) {
            this.amount = Optional.of(amount);
            return this;
        }

        public Transfer.Builder status(final Transfer.Status status) {
            this.status = status;
            return this;
        }

        public Transfer.Builder timeline(final Transfer.Timeline timeline) {
            this.timeline = timeline;
            return this;
        }

        public Transfer.Builder executionCondition(final Optional<Condition> executionCondition) {
            this.executionCondition = executionCondition;
            return this;
        }

        public Transfer.Builder expiresAt(final Optional<ZonedDateTime> expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Transfer.Builder memo(final Optional<String> memo) {
            this.memo = memo;
            return this;
        }

        public Transfer.Builder noteToSelf(final Optional<String> noteToSelf) {
            this.noteToSelf = noteToSelf;
            return this;
        }

        public Transfer.Builder additionalInfo(final Optional<String> additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }
    }

    /**
     * Timeline of a transferFunds's state transitions.  See commentary here for justification of these fields:
     * https://github.com/interledger/rfcs/pull/125/files#r88523131
     */
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Timeline {

        /**
         * The {@link ZonedDateTime} when the transferFunds was originally prepared.
         *
         * @return An instance of {@link ZonedDateTime}.
         */
        @NonNull
        @JsonProperty("created_at")
        private final ZonedDateTime createdAt;

        /**
         * The {@link ZonedDateTime} when the transferFunds was originally executed. This value MUST only appear if {@link
         * Transfer#status} is {@link Transfer.Status#EXECUTED}. This time MUST be equal to or later than {@link
         * #getCreatedAt}.
         *
         * @return An instance of {@link }.
         */
        @NonNull
        private final Optional<ZonedDateTime> executedAt;

        /**
         * The {@link }  when the transferFunds was originally rejected.  MUST appear if and only if {@link Transfer#status}
         * is {@link Transfer.Status#REJECTED}. This time MUST be equal to or later than {@link #getCreatedAt}.
         */
        @NonNull
        private final Optional<ZonedDateTime> rejectedAt;

        public Timeline(final Transfer.Timeline.Builder builder) {
            requireNonNull(builder);

            this.createdAt = builder.createdAt;
            this.executedAt = builder.executedAt;
            this.rejectedAt = builder.rejectedAt;
        }

        public static Transfer.Timeline.Builder builder(final ZonedDateTime proposedAt) {
            return new Transfer.Timeline.Builder(proposedAt);
        }

        @ToString
        @EqualsAndHashCode
        public static class Builder {

            private ZonedDateTime createdAt;

            private Optional<ZonedDateTime> executedAt;

            private Optional<ZonedDateTime> rejectedAt;

            public Builder(final ZonedDateTime createdAt) {
                this.createdAt = createdAt;
                this.executedAt = Optional.empty();
                this.rejectedAt = Optional.empty();
            }

            public Builder(final Timeline timeline) {
                requireNonNull(timeline);
                this.createdAt = timeline.createdAt;
                this.executedAt = timeline.executedAt;
                this.rejectedAt = timeline.rejectedAt;
            }

            public Transfer.Timeline build() {
                return new Transfer.Timeline(this);
            }

            public Transfer.Timeline.Builder createdAt(ZonedDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public Transfer.Timeline.Builder executedAt(Optional<ZonedDateTime> executedAt) {
                this.executedAt = executedAt;
                return this;
            }

            public Transfer.Timeline.Builder rejectedAt(Optional<ZonedDateTime> rejectedAt) {
                this.rejectedAt = rejectedAt;
                return this;
            }
        }
    }
}
