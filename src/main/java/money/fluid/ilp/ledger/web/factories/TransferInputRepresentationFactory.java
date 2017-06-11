package money.fluid.ilp.ledger.web.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.utils.IlpIdentifierTranslator;
import money.fluid.ilp.ledger.web.model.TransferInputRepresentation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A factory for assembling instances of {@link Transfer} toLedgerId various raw materials.
 */
@Component
@RequiredArgsConstructor
@ToString
public class TransferInputRepresentationFactory {

    private final IlpIdentifierTranslator ilpIdentifierTranslator;
    private final LedgerInfo ledgerInfo;

    /**
     * @param transferInput
     * @return
     */
    public TransferInputRepresentation convert(final TransferInput transferInput) {
        Objects.requireNonNull(transferInput);

        final TransferInputRepresentation.Builder builder = TransferInputRepresentation.builder();

        // TODO: Handle FQ URLs here...?
        transferInput.getDebitAccountId().ifPresent(
                debitAccountId -> builder.debitAccountUri(this.ilpIdentifierTranslator.toLocalLedgerAccountUri(debitAccountId))
        );
        transferInput.getCreditAccountId().ifPresent(
                creditAccountId -> builder.creditAccountUri(this.ilpIdentifierTranslator.toLocalLedgerAccountUri(creditAccountId))
        );

        // TODO: Is this accurate?
        transferInput.getAmount().ifPresent(amt -> builder.amount(new BigDecimal(amt.getNumber().toString())));

        transferInput.getExecutionCondition().ifPresent(builder::executionCondition);
        transferInput.getExpiresAt().ifPresent(builder::expiresAt);
        transferInput.getNoteToSelf().ifPresent(builder::noteToSelf);
        transferInput.getMemo().ifPresent(builder::memo);
        transferInput.getAdditionalInfo().ifPresent(builder::additionalInfo);
        transferInput.getExpiresAt().ifPresent(builder::expiresAt);

        return builder.build();
    }
}
