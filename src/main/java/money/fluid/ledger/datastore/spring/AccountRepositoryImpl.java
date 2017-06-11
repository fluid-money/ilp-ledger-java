package money.fluid.ledger.datastore.spring;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import money.fluid.ilp.ledger.exceptions.InvalidAccountException;
import money.fluid.ilp.ledger.exceptions.problems.InvalidLedgerAccountAmountProblem;
import money.fluid.ilp.ledger.exceptions.problems.transfers.AccountNotFoundProblem;
import money.fluid.ilp.ledger.exceptions.problems.transfers.InvalidTransferProblem;
import money.fluid.ilp.ledger.model.LedgerAccount;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ledger.datastore.jpa.AccountEntity;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * A repository class that provides APIs to do CRUD operations and some find operations like findAll, findOne, count.
 */
@Component
//@Qualifier("DbAccountManager")
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LedgerInfo ledgerInfo;

    @Override
    public Optional<LedgerAccount> getAccount(final LedgerAccountId ledgerAccountId) throws InvalidAccountException {
        final String currencyCode = ledgerInfo.getCurrencyUnit();
        final AccountEntity accountEntity = Optional.ofNullable(
                accountRepository.findOne(ledgerAccountId.value())).orElseThrow(
                () -> new AccountNotFoundProblem(ledgerAccountId));

        final MonetaryAmount monetaryAmount = Money.of(accountEntity.getAmount(), currencyCode);
        return Optional.of(new LedgerAccount(LedgerAccountId.of(accountEntity.getId()), monetaryAmount));
    }

    @Override
    @Transactional // Any failures will cause all DB transactions to be rolled-back.
    public void transferFunds(
            final LedgerAccountId localSourceAccountId, final LedgerAccountId localDestinationAccountId,
            final MonetaryAmount amount
    ) {
        Objects.requireNonNull(localSourceAccountId);
        Objects.requireNonNull(localDestinationAccountId);
        Objects.requireNonNull(amount);

        Preconditions.checkArgument(
                amount.getCurrency().equals(this.ledgerInfo.getCurrencyUnit()),
                "Transfers must specify the same currency code as this Ledger!"
        );

        if (localSourceAccountId.equals(localDestinationAccountId)) {
            throw new InvalidTransferProblem(
                    localSourceAccountId, localDestinationAccountId,
                    "Transfers must be executed across different accounts!"
            );
        }

        final LedgerAccount sourceAccount = this.getAccount(localSourceAccountId).orElseThrow(
                () -> new RuntimeException(
                        "No account exists for Source LedgerAccountId: " + localSourceAccountId));

        final LedgerAccount destinationAccount = this.getAccount(localDestinationAccountId).orElseThrow(
                () -> new RuntimeException(
                        "No account exists for Destination LedgerAccountId: " + localDestinationAccountId));

        // Take money out of the source account...
        final LedgerAccount updatedSourceAccount = sourceAccount.withNewBalance(
                sourceAccount.getBalance().subtract(amount));

        // Disallow the account updatedSourceAccount from going negative...
        if (updatedSourceAccount.getBalance().isNegative()) {
            throw new InvalidLedgerAccountAmountProblem(updatedSourceAccount.getLedgerAccountId());
        }

        // Put money into the destination account...
        final LedgerAccount updatedDestinationAccount = destinationAccount.withNewBalance(
                destinationAccount.getBalance().add(amount));

        // Disallow the account updatedDestinationAccount from going negative...
        if (updatedDestinationAccount.getBalance().isNegative()) {
            throw new InvalidLedgerAccountAmountProblem(updatedDestinationAccount.getLedgerAccountId());
        }

        this.accountRepository.save(
                Lists.newArrayList(toAccountEntity(updatedSourceAccount), toAccountEntity(updatedDestinationAccount)));
    }


    private AccountEntity toAccountEntity(final LedgerAccount ledgerAccount) {
        Objects.requireNonNull(ledgerAccount);

        return new AccountEntity(
                ledgerAccount.getLedgerAccountId().value(),
                new BigDecimal(ledgerAccount.getBalance().getNumber().toString())
                //ledgerAccount.getBalance().getCurrency().getCurrencyCode()
        );
    }

}
