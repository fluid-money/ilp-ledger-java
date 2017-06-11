package money.fluid.ilp.ledger.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Transfer.Status;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.interledger.cryptoconditions.Condition;

import javax.money.MonetaryAmount;
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
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class TransferInput {

    /**
     * A unique identifier for this {@link TransferInput}.
     */
    private final Optional<LedgerTransferId> id;

    /**
     * The {@link LedgerAccountId} of the account debited by this transferFunds.
     */
    private final Optional<LedgerAccountId> debitAccountId;

    /**
     * The {@link LedgerAccountId} of the account credited by this transferFunds.
     */
    private final Optional<LedgerAccountId> creditAccountId;

    /**
     * The amount of the currency/asset being transferred.
     */
    private final Optional<MonetaryAmount> amount;

//    /**
//     * The optionally specified {@link Status} of this transferFunds (can be either CREATED or PROPOSED).
//     */
//    private final Optional<Status> status;

    /**
     * The condition for executing the transferFunds. If omitted, the transferFunds executes unconditionally.
     */
    private final Optional<Condition> executionCondition;

    /**
     * The date when the transferFunds expires and can no longer be executed.
     */
    private final Optional<ZonedDateTime> expiresAt;

    /**
     * Arbitrary data provided by the debitAccountId for the creditAccountUri. Also, the connector stores the ILP Header in
     * this field.
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

    public TransferInput(final TransferInput.Builder builder) {
        Objects.requireNonNull(builder);

        this.id = builder.id;
        this.debitAccountId = builder.debitAccountId;
        this.creditAccountId = builder.creditAccountId;
        this.amount = builder.amount;
        //this.status = builder.status;
        this.executionCondition = builder.executionCondition;
        this.expiresAt = builder.expiresAt;
        this.memo = builder.getMemo();
        this.noteToSelf = builder.getNoteToSelf();
        this.additionalInfo = builder.getAdditionalInfo();
    }

    public static TransferInput.Builder builder() {
        return new TransferInput.Builder();
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Builder {
        private Optional<LedgerTransferId> id;

        private Optional<LedgerAccountId> debitAccountId;

        private Optional<LedgerAccountId> creditAccountId;

        private Optional<MonetaryAmount> amount;

        private Optional<Status> status;

        private Optional<Condition> executionCondition;

        private Optional<ZonedDateTime> expiresAt;

        private Optional<String> memo;

        private Optional<String> noteToSelf;

        private Optional<String> additionalInfo;

        public Builder() {
            this.id = Optional.empty();
            this.status = Optional.empty();
            this.debitAccountId = Optional.empty();
            this.creditAccountId = Optional.empty();
            this.amount = Optional.empty();
            this.executionCondition = Optional.empty();
            this.expiresAt = Optional.empty();
            this.memo = Optional.empty();
            this.noteToSelf = Optional.empty();
            this.additionalInfo = Optional.empty();
        }

        public TransferInput build() {
            return new TransferInput(this);
        }

        public TransferInput.Builder id(final LedgerTransferId id) {
            this.id = Optional.of(id);
            return this;
        }

        public TransferInput.Builder debitAccountId(final LedgerAccountId debitAccountId) {
            this.debitAccountId = Optional.of(debitAccountId);
            return this;
        }

        public TransferInput.Builder creditAccountId(final LedgerAccountId creditAccountId) {
            this.creditAccountId = Optional.of(creditAccountId);
            return this;
        }

        public TransferInput.Builder amount(final MonetaryAmount amount) {
            this.amount = Optional.of(amount);
            return this;
        }

        public TransferInput.Builder status(final Status status) {
            this.status = Optional.of(status);
            return this;
        }

        public TransferInput.Builder executionCondition(final Condition executionCondition) {
            this.executionCondition = Optional.of(executionCondition);
            return this;
        }

        public TransferInput.Builder expiresAt(final ZonedDateTime expiresAt) {
            this.expiresAt = Optional.of(expiresAt);
            return this;
        }

        public TransferInput.Builder memo(final String memo) {
            this.memo = Optional.of(memo);
            return this;
        }

        public TransferInput.Builder noteToSelf(final String noteToSelf) {
            this.noteToSelf = Optional.of(noteToSelf);
            return this;
        }

        public TransferInput.Builder additionalInfo(final String additionalInfo) {
            this.additionalInfo = Optional.of(additionalInfo);
            return this;
        }
    }
}
