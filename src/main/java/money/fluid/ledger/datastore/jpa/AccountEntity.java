package money.fluid.ledger.datastore.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * An entity for persisting account information to a JPA-compatible datastore.
 */
@Entity
@Table(name = "ACCOUNT")
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class AccountEntity implements Serializable {

    @Id
    private final String id;

    @Column(name = "amount", nullable = false)
    private final BigDecimal amount;

//    @Column(name = "currency_code", nullable = false)
//    private final String currencyCode;

    /**
     * No-args Constructor.  Exists only for Hibernate...
     */
    AccountEntity() {
        this.id = null;
        this.amount = null;
        //this.currencyCode = null;
    }

    AccountEntity(final Builder builder) {
        this.id = builder.id;
        this.amount = builder.amount;
        //this.currencyCode = builder.currencyCode;
    }

    public static class Builder {
        private String id;

        private BigDecimal amount;

        private String currencyCode;

        public Builder(final AccountEntity accountEntity) {
            Objects.requireNonNull(accountEntity);
            this.id = accountEntity.getId();
            this.amount = accountEntity.getAmount();
            // this.currencyCode = accountEntity.getCurrencyCode();
        }

        public AccountEntity build() {
            return new AccountEntity(this);
        }

        public Builder withId(LedgerAccountId ledgerAccountId) {
            this.id = ledgerAccountId.value();
            return this;
        }

        public Builder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

//        public Builder withCurrencyCode(String currencyCode) {
//            this.currencyCode = currencyCode;
//            return this;
//        }

    }
}
