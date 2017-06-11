package money.fluid.ilp.ledger.datastore.jpa.timelines;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Transfer.Status;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * An entity for persisting account information to a JPA-compatible datastore.
 */
@Entity
@Table(name = "TRANSFER")
@Builder
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class TransferEntity implements Serializable {

    // We don't auto-generate the Transfer identifier since clients may specify it.
    @Id
    @Column(name = "ID", length = 64)
    private final String id;

    @Column(name = "DEBIT_ACCOUNT_ID", length = 128)
    private final String debitAccountId;

    @Column(name = "CREDIT_ACCOUNT_ID", length = 128)
    private final String creditAccountId;

    @Column(name = "AMOUNT_NUMBER")
    private final BigDecimal amount;

    @Column(name = "AMOUNT_CURRENCY_CODE", length = 5)
    private final String currencyCode;

    @Column(name = "STATUS", nullable = false)
    private final Status status;

    // TODO: Implement this!
//    @Column(name = "CONDITION", nullable = false)
//    private final Condition executionCondition;

    @Column(name = "EXPIRES_AT")
    private final ZonedDateTime expiresAt;

    @Column(name = "TIMELINE")
    private final TimelineEntity timeline;

    @Column(name = "MEMO", length = 2048)
    private final String memo;

    @Column(name = "NOTE_TO_SELF", length = 2048)
    private final String noteToSelf;

    @Column(name = "ADDITIONAL_INFO", length = 2048)
    private final String additionalInfo;

    /**
     * No-args Constructor.  Exists only for Hibernate...
     */
    TransferEntity() {
        this.id = null;
        this.debitAccountId = null;
        this.creditAccountId = null;
        this.amount = null;
        this.currencyCode = null;
        this.status = null;
        this.expiresAt = null;
        this.timeline = null;
        this.memo = null;
        this.noteToSelf = null;
        this.additionalInfo = null;
    }

    public TransferEntity(final Builder builder) {
        Objects.requireNonNull(builder);

        this.id = builder.id;
        this.debitAccountId = builder.debitAccountId;
        this.creditAccountId = builder.creditAccountId;
        this.amount = builder.amount;
        this.currencyCode = builder.currencyCode;
        this.status = builder.status;
        this.expiresAt = builder.expiresAt;
        this.timeline = builder.timeline;
        this.memo = builder.memo;
        this.noteToSelf = builder.noteToSelf;
        this.additionalInfo = builder.additionalInfo;
    }

    @ToString
    public static class Builder {

        private String id;

        private String debitAccountId;

        private String creditAccountId;

        private BigDecimal amount;

        private String currencyCode;

        private Status status;

//    @Column(name = "CONDITION", nullable = false)
//    private final Condition executionCondition;

        private ZonedDateTime expiresAt;

        private TimelineEntity timeline;

        private String memo;

        private String noteToSelf;

        private String additionalInfo;

        public Builder(final TransferEntity transferEntity) {
            Objects.requireNonNull(transferEntity);

            this.id = transferEntity.getId();
            this.debitAccountId = transferEntity.getDebitAccountId();
            this.creditAccountId = transferEntity.getCreditAccountId();
            this.amount = transferEntity.getAmount();
            this.currencyCode = transferEntity.getCurrencyCode();
            this.status = transferEntity.getStatus();
            this.expiresAt = transferEntity.getExpiresAt();
            this.timeline = transferEntity.getTimeline();
            this.memo = transferEntity.getMemo();
            this.noteToSelf = transferEntity.getNoteToSelf();
            this.additionalInfo = transferEntity.getAdditionalInfo();
        }

        public TransferEntity build() {
            return new TransferEntity(this);
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withDebitAccountId(String debitAccountId) {
            this.debitAccountId = debitAccountId;
            return this;
        }

        public Builder withCreditAccountId(String creditAccountId) {
            this.creditAccountId = creditAccountId;
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder withCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }

        public Builder withExpiresAt(ZonedDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder withTimeline(TimelineEntity timeline) {
            this.timeline = timeline;
            return this;
        }

        public Builder withMemo(String memo) {
            this.memo = memo;
            return this;
        }

        public Builder withNoteToSelf(String noteToSelf) {
            this.noteToSelf = noteToSelf;
            return this;
        }

        public Builder withAdditionalInfo(String additionalInfo) {
            this.additionalInfo = additionalInfo;
            return this;
        }
    }
}
