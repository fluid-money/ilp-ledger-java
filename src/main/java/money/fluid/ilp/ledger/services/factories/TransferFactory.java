package money.fluid.ilp.ledger.services.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TimelineEntity;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferEntity;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Builder;
import money.fluid.ilp.ledger.model.Transfer.Status;
import money.fluid.ilp.ledger.model.Transfer.Timeline;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import money.fluid.ilp.ledger.utils.IlpIdentifierTranslator;
import money.fluid.ilp.ledger.utils.MoneyUtils;
import money.fluid.ilp.ledger.web.model.TransferInputRepresentation;
import money.fluid.ilp.ledger.web.model.TransferRepresentation;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * A factory for assembling instances of {@link Transfer} toLedgerId various raw materials.
 */
@Service
@RequiredArgsConstructor
@ToString
public class TransferFactory {

    private final LedgerMetadataService ledgerMetadataService;
    private final IlpIdentifierTranslator ilpIdentifierTranslator;

    /**
     * Construct a {@link Transfer} toLedgerId the supplied {@code transferInput}.
     *
     * @param transferInput An instance of {@link TransferInputRepresentation}.
     * @return A newly constructed instance of {@link Transfer}.
     */
    public Transfer.Builder convert(final TransferInput transferInput) {
        requireNonNull(transferInput);

        final Transfer.Builder transferBuilder = Transfer.builder();

        transferBuilder.debitAccountId(transferInput.getDebitAccountId());
        transferBuilder.creditAccountId(transferInput.getCreditAccountId());
        transferBuilder.amount(transferInput.getAmount().orElse(
                MoneyUtils.zero(this.ledgerMetadataService.getLedgerMetadata().getAssetInfo().getCode())));

        // Use the id toLedgerId the client, or else generate a new one.
        final LedgerTransferId ledgerTransferId = transferInput.getId().orElseGet(
                () -> LedgerTransferId.of(UUID.randomUUID().toString()));
        transferBuilder.id(ledgerTransferId);

        final Status status = Status.PREPARED;
        transferBuilder.status(status);

        // Setup the initial timeline...
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Z"));
        final Timeline.Builder timelineBuilder = Timeline.builder(now).createdAt(now);
        transferBuilder.timeline(timelineBuilder.build());

        transferBuilder.memo(transferInput.getMemo());
        transferBuilder.noteToSelf(transferInput.getNoteToSelf());
        transferBuilder.additionalInfo(transferInput.getAdditionalInfo());

        return transferBuilder;
    }

    private MonetaryAmount toLedgerCurrency(final BigDecimal amount) {
        requireNonNull(amount);

        final String currencyCode = this.ledgerMetadataService.getLedgerMetadata().getAssetInfo().getCode();
        return MoneyUtils.toMonetaryAmount(amount.toPlainString(), currencyCode);
    }

    /**
     * Construct an empty {@link TransferRepresentation}.
     *
     * @return
     */
    public Transfer construct() {
        final Transfer.Builder builder = Transfer.builder();

        builder.id(LedgerTransferId.of(UUID.randomUUID().toString()));
        builder.status(Status.PREPARED);
        builder.timeline(Timeline.builder(ZonedDateTime.now(ZoneId.of("Z"))).build());

        return builder.build();
    }

    public Transfer convert(final TransferEntity transferEntity) {
        requireNonNull(transferEntity);
        final Builder builder = Transfer.builder()
                .id(LedgerTransferId.of(transferEntity.getId().toString()))
                .debitAccountId(
                        Optional.ofNullable(transferEntity.getDebitAccountId()).map(id -> LedgerAccountId.of(id)))
                .creditAccountId(
                        Optional.ofNullable(transferEntity.getCreditAccountId()).map(id -> LedgerAccountId.of(id)))
                // Amount populated below...
                .status(transferEntity.getStatus())

                .memo(Optional.ofNullable(transferEntity.getMemo()))
                .additionalInfo(Optional.ofNullable(transferEntity.getAdditionalInfo()))
                .noteToSelf(Optional.ofNullable(transferEntity.getNoteToSelf()))

                //.executionCondition(transferEntity.getCondition())
                //.expiresAt(transferEntity.getExpiresAt)

                .timeline(convert(transferEntity.getTimeline()));


        final Optional<BigDecimal> amount = Optional.ofNullable(transferEntity.getAmount());
        final Optional<String> currencyCode = Optional.ofNullable(transferEntity.getCurrencyCode());
        if (amount.isPresent() && currencyCode.isPresent()) {
            builder.amount(Money.of(amount.get(), currencyCode.get()));
        }

        return builder.build();

    }

    public Timeline convert(final TimelineEntity timelineEntity) {
        requireNonNull(timelineEntity);

        return Timeline.builder(timelineEntity.getCreatedAt())
                .executedAt(Optional.ofNullable(timelineEntity.getExecutedAt()))
                .rejectedAt(Optional.ofNullable(timelineEntity.getRejectedAt()))
                .build();
    }
//    public Transfer withNewStatus(final Transfer.Status newLedgerTransferStatus) {
//        Objects.requireNonNull(newLedgerTransferStatus);
//
//        switch (newLedgerTransferStatus) {
//            case PROPOSED: {
//                // Throw an InterledgerException instead.
//                throw new RuntimeException("new status " + newLedgerTransferStatus + " not allowed");
//            }
//            case CREATED: {
//                Preconditions.checkArgument(this.status == LedgerTransferStatus.PROPOSED);
//                Preconditions.checkArgument(!optPreparedAt.isPresent());
//                Preconditions.checkArgument(!optExecutedAt.isPresent());
//                Preconditions.checkArgument(!optRejectedAt.isPresent());
//                break;
//            }
//            case EXECUTED: {
//                Preconditions.checkArgument(this.status == LedgerTransferStatus.CREATED);
//                Preconditions.checkArgument(optPreparedAt.isPresent());
//                Preconditions.checkArgument(!optExecutedAt.isPresent());
//                Preconditions.checkArgument(!optRejectedAt.isPresent());
//                break;
//            }
//            case REJECTED: {
//                Preconditions.checkArgument(this.status != LedgerTransferStatus.EXECUTED);
//                Preconditions.checkArgument(optPreparedAt.isPresent());
//                Preconditions.checkArgument(!optExecutedAt.isPresent());
//                Preconditions.checkArgument(!optRejectedAt.isPresent());
//                break;
//            }
//            default: {
//                throw new RuntimeException("Unhandled new status " + newLedgerTransferStatus);
//            }
//        }
//
//        return new Transfer(
//                this.value(),
//                this.getLedgerId(),
//                newLedgerTransferStatus,
//                this.getProposedAt(),
//                this.getOptCondition(),
//                this.getOptExtraInfo(),
//                this.getOptExecutionFulfillment(),
//                this.getOptCancelationFulfillment(),
//                this.getOptExpirationAt(),
//                this.getOptPreparedAt(),
//                this.getOptExecutedAt(),
//                this.getOptRejectedAt()
//        );
//    }

}
