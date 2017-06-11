package money.fluid.ilp.ledger.web.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Timeline;
import money.fluid.ilp.ledger.utils.IlpIdentifierTranslator;
import money.fluid.ilp.ledger.web.model.TransferRepresentation;
import money.fluid.ilp.ledger.web.model.TransferRepresentation.TimelineRepresentation;
import org.javamoney.moneta.spi.MoneyUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * A factory for assembling instances of {@link Transfer} toLedgerId various raw materials.
 */
@Component
@RequiredArgsConstructor
@ToString
public class TransferRepresentationFactory {

    private final IlpIdentifierTranslator ilpIdentifierTranslator;

//    /**
//     * Construct an empty {@link TransferRepresentation}.
//     *
//     * @return
//     */
//    public TransferRepresentation construct() {
//        final TransferRepresentation.Builder builder = TransferRepresentation.builder();
//
//        builder.id(UUID.randomUUID().toString());
//        builder.status(Status.PROPOSED.name());
//        builder.timeline(toTimelineRepresentation(Timeline.builder(ZonedDateTime.now(ZoneId.of("Z"))).build()));
//
//        return builder.build();
//    }

    /**
     * @param transfer
     * @return
     */
    public TransferRepresentation construct(final Transfer transfer) {
        Objects.requireNonNull(transfer);

        final TransferRepresentation.Builder builder = TransferRepresentation.builder();

        builder.id(transfer.getId().value());

        // TODO: Handle FQ URLs here...?
        transfer.getDebitAccountId().ifPresent((id) -> builder.debitAccount(
                this.ilpIdentifierTranslator.toLocalLedgerAccountUri(id)
        ));
        transfer.getCreditAccountId().ifPresent((id) -> builder.creditAccount(
                this.ilpIdentifierTranslator.toLocalLedgerAccountUri(id)
        ));

        transfer.getAmount()
                .map(amount -> amount.getNumber())
                .ifPresent((numberValue) -> builder.amount(MoneyUtils.getBigDecimal(numberValue)));

        builder.status(transfer.getStatus().name());

        builder.noteToSelf(transfer.getNoteToSelf());
        builder.executionCondition(transfer.getExecutionCondition());
        builder.expiresAt(transfer.getExpiresAt());
        builder.additionalInfo(transfer.getAdditionalInfo());
        builder.noteToSelf(transfer.getNoteToSelf());
        builder.memo(transfer.getMemo());

        builder.timeline(toTimelineRepresentation(transfer.getTimeline()));

        return builder.build();
    }

    private TimelineRepresentation toTimelineRepresentation(final Timeline timeline) {
        Objects.requireNonNull(timeline);

        return TimelineRepresentation.builder(timeline.getCreatedAt())
                .executedAt(timeline.getExecutedAt())
                .rejectedAt(timeline.getRejectedAt())
                .build();
    }
}
