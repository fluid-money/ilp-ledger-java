package money.fluid.ilp.ledger.services.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TimelineEntity;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferEntity;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Timeline;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
@ToString
public class TransferEntityFactory {

    private final LedgerMetadataService ledgerMetadataService;

    public TransferEntity convert(final Transfer transfer) {
        requireNonNull(transfer);
        return TransferEntity.builder()
                .id(transfer.getId().value())
                .debitAccountId(
                        transfer.getDebitAccountId().map(id -> id.value()).orElse(null)
                )
                .creditAccountId(
                        transfer.getCreditAccountId().map(id -> id.value()).orElse(null)
                )
                .amount(transfer.getAmount().map(amt -> new BigDecimal(amt.getNumber().toString())).orElse(
                        BigDecimal.ZERO))
                // TODO: Get ledger currency code...
                .currencyCode(transfer.getAmount().map(amt -> amt.getCurrency().getCurrencyCode()).orElse(
                        ledgerMetadataService.getLedgerMetadata().getAssetInfo().getCode()))

                .status(transfer.getStatus())

                .memo(transfer.getMemo().orElse(null))
                .additionalInfo(transfer.getAdditionalInfo().orElse(null))
                .noteToSelf(transfer.getNoteToSelf().orElse(null))

                // TODO:
                //.executionCondition(transferEntity.getCondition())
                //.expiresAt(transferEntity.getExpiresAt)

                .timeline(convert(transfer.getTimeline()))
                .build();
    }

    public TimelineEntity convert(final Timeline timeline) {
        requireNonNull(timeline);

        return TimelineEntity.builder()
                .createdAt(timeline.getCreatedAt())
                .executedAt(timeline.getExecutedAt().orElse(null))
                .rejectedAt(timeline.getRejectedAt().orElse(null))
                .build();
    }
}
