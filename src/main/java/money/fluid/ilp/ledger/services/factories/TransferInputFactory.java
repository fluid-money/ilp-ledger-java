package money.fluid.ilp.ledger.services.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Status;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import money.fluid.ilp.ledger.utils.IlpIdentifierTranslator;
import money.fluid.ilp.ledger.web.model.TransferInputRepresentation;
import org.javamoney.moneta.function.MoneyProducer;
import org.springframework.stereotype.Component;

import javax.money.Monetary;
import java.util.Objects;
import java.util.UUID;

/**
 * A factory for assembling instances of {@link Transfer} toLedgerId various raw materials.
 */
@Component
@RequiredArgsConstructor
@ToString
public class TransferInputFactory {

    private final IlpIdentifierTranslator ilpIdentifierTranslator;
    private final LedgerInfo ledgerInfo;

    /**
     * @param transferInputRepresentation
     * @return
     */
    public TransferInput convert(final TransferInputRepresentation transferInputRepresentation) {
        Objects.requireNonNull(transferInputRepresentation);

        final TransferInput.Builder builder = TransferInput.builder();

        // TODO: Handle FQ URLs here...?
        transferInputRepresentation.getDebitAccountId().ifPresent(
                debitAccountId -> builder.debitAccountId(
                        this.ilpIdentifierTranslator.toLedgerAccountId(debitAccountId)
                )
        );
        transferInputRepresentation.getCreditAccountId().ifPresent(
                creditAccountId -> builder.creditAccountId(
                        this.ilpIdentifierTranslator.toLedgerAccountId(creditAccountId)
                )
        );

        transferInputRepresentation.getAmount().ifPresent(
                amt -> builder.amount(
                        new MoneyProducer().create(
                                Monetary.getCurrency(ledgerInfo.getCurrencyUnit()),
                                amt
                        )
                )
        );

        // Use the id toLedgerId the client, or else generate a new one.
        final LedgerTransferId ledgerTransferId = transferInputRepresentation.getClientId()
                .map(cid -> LedgerTransferId.of(cid))
                .orElse(LedgerTransferId.of(UUID.randomUUID().toString()));
        builder.id(ledgerTransferId);

        builder.status(Status.PREPARED);

        transferInputRepresentation.getExecutionCondition().ifPresent(builder::executionCondition);
        transferInputRepresentation.getExpiresAt().ifPresent(builder::expiresAt);
        transferInputRepresentation.getNoteToSelf().ifPresent(builder::noteToSelf);
        transferInputRepresentation.getMemo().ifPresent(builder::memo);
        transferInputRepresentation.getAdditionalInfo().ifPresent(builder::additionalInfo);
        transferInputRepresentation.getExpiresAt().ifPresent(builder::expiresAt);

        return builder.build();
    }
}