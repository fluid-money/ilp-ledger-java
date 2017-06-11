package money.fluid.ilp.ledger.services.transfer;

import com.google.common.annotations.VisibleForTesting;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TimelineEntity;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferEntity;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferRepository;
import money.fluid.ilp.ledger.exceptions.problems.transfers.AccountNotFoundProblem;
import money.fluid.ilp.ledger.exceptions.problems.transfers.TransferNotFoundProblem;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.Transfer.Status;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import money.fluid.ilp.ledger.services.LedgerTransferService;
import money.fluid.ilp.ledger.services.factories.TransferEntityFactory;
import money.fluid.ilp.ledger.services.factories.TransferFactory;
import money.fluid.ilp.ledger.utils.MoneyUtils;
import money.fluid.ledger.datastore.jpa.AccountEntity;
import money.fluid.ledger.datastore.spring.AccountRepository;
import org.ilpx.ledger.core.LedgerEventPublisher;
import org.ilpx.ledger.core.events.LedgerTransferCreatedEvent;
import org.ilpx.ledger.core.events.LedgerTransferUpdatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * An implementation of {@link LedgerTransferService} based upon SpringData that provides business logic for
 * executing transfers in a persistent manner.
 */
@Service
@RequiredArgsConstructor
@ToString
public class SpringDataLedgerTransferService implements LedgerTransferService {

    @NonNull
    private final LedgerMetadataService ledgerMetadataService;

    @NonNull
    private final LedgerInfo ledgerInfo;

    @NonNull
    private final TransferValidator transferValidator;

    @NonNull
    private final TransferFactory transferFactory;

    @NonNull
    private final TransferRepository transferRepository;

    @NonNull
    private final AccountRepository accountRepository;

    @NonNull
    private final TransferEntityFactory transferEntityFactory;

    @NonNull
    private final LedgerEventPublisher ledgerEventPublisher;

//    @Override
//    public Transfer createTransfer() {
//        // Store the transferFunds into the store.
//        final Transfer transferFunds = this.transferFactory.construct();
//        final TransferEntity transferEntity = this.transferEntityFactory.convert(transferFunds);
//
//        final TransferEntity savedTransferEntity = this.transferRepository.save(transferEntity);
//
//        if (savedTransferEntity.getStatus() == Status.CREATED) {
//            final LedgerAccountId localSourceAccountId = savedTransferEntity.getDebitAccountId() == null ? null :
//                    LedgerAccountId.of(savedTransferEntity.getDebitAccountId());
//            ledgerEventPublisher.publish(toLedgerTransferCreatedEvent(localSourceAccountId, savedTransferEntity));
//
//            final LedgerAccountId localDestinationAccountId = savedTransferEntity.getCreditAccountId() == null ? null :
//                    LedgerAccountId.of(savedTransferEntity.getCreditAccountId());
//            ledgerEventPublisher.publish(toLedgerTransferCreatedEvent(localDestinationAccountId, savedTransferEntity));
//        }
//
//        // return the saved Transfer.
//        return this.transferFactory.convert(savedTransferEntity);
//    }


    @Override
    public Transfer createTransfer(final TransferInput transferInput) {
        requireNonNull(transferInput);

        // Validate the incoming TransferInput...
        this.transferValidator.validateTransferInputForCreate(transferInput);

        final Transfer incomingTransfer = this.transferFactory.convert(transferInput).build();
        final TransferEntity transferEntity = this.transferEntityFactory.convert(incomingTransfer);
        final TransferEntity savedTransferEntity = this.transferRepository.save(transferEntity);

        // Execute the transferFunds immediately if it's optimistic...
        if (this.transferValidator.isOptimisticTransfer(transferInput)) {
            final TransferEntity executedTransfer = this.executeTransfer(
                    LedgerTransferId.of(savedTransferEntity.getId()));
            {
                // Notify for Credit Account...
                final LedgerTransferCreatedEvent event = this.toLedgerTransferCreatedEvent(
                        LedgerAccountId.of(transferEntity.getCreditAccountId()),
                        executedTransfer
                );
                ledgerEventPublisher.publish(event);
            }

            {
                // Notify for Debit Account...
                final LedgerTransferCreatedEvent event = this.toLedgerTransferCreatedEvent(
                        LedgerAccountId.of(transferEntity.getDebitAccountId()),
                        executedTransfer
                );
                ledgerEventPublisher.publish(event);
            }

            return this.transferFactory.convert(executedTransfer);
        } else {

            ///////////////////////////////////
            // Not Optimistic, so do nothing for the payment, but peform notifications...
            ///////////////////////////////////
            {
                // Notify for Credit Account...
                final LedgerTransferCreatedEvent event = this.toLedgerTransferCreatedEvent(
                        LedgerAccountId.of(transferEntity.getCreditAccountId()),
                        savedTransferEntity
                );
                ledgerEventPublisher.publish(event);
            }
            {
                // Notify for Debit Account...
                final LedgerTransferCreatedEvent event = this.toLedgerTransferCreatedEvent(
                        LedgerAccountId.of(transferEntity.getDebitAccountId()),
                        savedTransferEntity
                );
                ledgerEventPublisher.publish(event);
            }

            return this.transferFactory.convert(savedTransferEntity);
        }
    }

    @VisibleForTesting
    @Transactional
    protected TransferEntity executeTransfer(final LedgerTransferId ledgerTransferId) {
        requireNonNull(ledgerTransferId);

        final TransferEntity transferEntity = Optional.ofNullable(
                this.transferRepository.findOne(ledgerTransferId.value())).orElseThrow(
                () -> new TransferNotFoundProblem(ledgerTransferId));

        final AccountEntity sourceAccount = Optional.ofNullable(
                this.accountRepository.findOne(transferEntity.getDebitAccountId())).orElseThrow(
                () -> new AccountNotFoundProblem(LedgerAccountId.of(transferEntity.getDebitAccountId())));

        final AccountEntity destinationAccount = Optional.ofNullable(
                this.accountRepository.findOne(transferEntity.getCreditAccountId())).orElseThrow(
                () -> new AccountNotFoundProblem(LedgerAccountId.of(transferEntity.getCreditAccountId())));

        return this.executeTransferHelper(
                transferEntity, sourceAccount, destinationAccount);
    }

    @VisibleForTesting
    @Transactional
    protected TransferEntity executeTransferHelper(
            final TransferEntity transferEntity, final AccountEntity sourceAccountEntity,
            final AccountEntity destinationAccountEntity
    ) {
        requireNonNull(transferEntity);
        requireNonNull(sourceAccountEntity);
        requireNonNull(destinationAccountEntity);

        // TODO: Prevent accounts from going negative...

        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Z"));
        final TimelineEntity timelineEntity = TimelineEntity.builder()
                .createdAt(transferEntity.getTimeline().getCreatedAt())
                .executedAt(now)
                //.rejectedAt
                .build();

        final TransferEntity executedTransferEntity = new TransferEntity.Builder(transferEntity)
                .withStatus(Status.EXECUTED)
                .withTimeline(timelineEntity)
                .build();

        final AccountEntity updatedSource = new AccountEntity.Builder(sourceAccountEntity)
                .withAmount(sourceAccountEntity.getAmount().subtract(transferEntity.getAmount())).build();

        final AccountEntity updatedDestination = new AccountEntity.Builder(destinationAccountEntity)
                .withAmount(destinationAccountEntity.getAmount().add(transferEntity.getAmount())).build();

        this.accountRepository.save(updatedSource);
        this.accountRepository.save(updatedDestination);
        return this.transferRepository.save(executedTransferEntity);
    }

    @Override
    public Optional<Transfer> getTransfer(final LedgerTransferId ledgerTransferId) {
        return Optional.ofNullable(this.transferRepository.findOne(ledgerTransferId.value())).map(
                transferEntity -> transferFactory.convert(transferEntity));
    }

    @Override
    public Transfer updateTransfer(
            final LedgerTransferId ledgerTransferId, final TransferInput transferInput
    ) {
        requireNonNull(ledgerTransferId);
        requireNonNull(transferInput);

        final Transfer transfer = this.getTransfer(ledgerTransferId).orElseThrow(
                () -> new TransferNotFoundProblem(ledgerTransferId));

        // Validation...
        this.transferValidator.validateTransferInputForUpdate(transfer, transferInput);

        final Transfer proposedTransfer = this.merge(transfer, transferInput);

        final TransferEntity proposedTransferEntity = this.transferEntityFactory.convert(proposedTransfer);
        final TransferEntity savedProposedTransferEntity = this.transferRepository.save(proposedTransferEntity);

        // Notify event listeners of the Update...
        final LedgerAccountId ledgerAccountId = savedProposedTransferEntity.getDebitAccountId() == null ? null :
                LedgerAccountId.of(savedProposedTransferEntity.getDebitAccountId());
        if (ledgerAccountId != null) {
            ledgerEventPublisher.publish(
                    toLedgerTransferCreatedEvent(ledgerAccountId, savedProposedTransferEntity));
        }

        final Transfer savedProposedTransfer = this.transferFactory.convert(savedProposedTransferEntity);
        return savedProposedTransfer;

//        // If the Status of the transferFunds being updated is CREATED, and we reach this line of code, then the Transfer
//        // should be automatically prepared
//        final boolean shouldExecute = false; // TODO:
//
//        // If the two statuses are not equal, then we should prepare the transferFunds...
//        // This involves an additional hit to the DB, but this is desirable for auditing.
//        if (!shouldExecute) {
//            return savedProposedTransfer;
//        } else {
//            final Transfer preparedTransfer = this.prepareTransfer(ledgerTransferId);
//
//            // Execute the transferFunds immediately if it's an optimistic one...
//            // This involves an additional hit to the DB, but this is desirable for auditing.
//            if (transferFunds.isOptimisticMode()) {
//                return this.executeTransfer(ledgerTransferId);
//            } else {
//                return preparedTransfer;
//            }
//        }
    }


    /**
     * Update {@code transferFunds} with data contained in the supplied {@link TransferInput} called {@code transferInput}.
     * <p>
     * This method assumes that the existing {@code transferFunds} is in a state that is allowed to be updated.
     *
     * @param transfer
     * @param transferInput
     * @return A new instance of {@link Transfer} that contains updated information found in {@code transferInput}.
     */
    @VisibleForTesting
    protected Transfer merge(final Transfer transfer, final TransferInput transferInput) {
        Objects.requireNonNull(transfer);
        Objects.requireNonNull(transferInput);

        final Transfer.Builder transferBuilder = new Transfer.Builder(transfer);

        transferBuilder.debitAccountId(transferInput.getDebitAccountId());
        transferBuilder.creditAccountId(transferInput.getCreditAccountId());
        transferBuilder.amount(transferInput.getAmount().orElse(MoneyUtils.zero(
                this.ledgerMetadataService.getLedgerMetadata().getAssetInfo().getCode())));
        //transferInput.getStatus().ifPresent(transferBuilder::status);

        transferBuilder.executionCondition(transferInput.getExecutionCondition());
        transferBuilder.expiresAt(transferInput.getExpiresAt());

        transferBuilder.additionalInfo(transferInput.getAdditionalInfo());
        transferBuilder.noteToSelf(transferInput.getNoteToSelf());
        transferBuilder.memo(transferInput.getMemo());

        return transferBuilder.build();
    }

//    /**
//     * Notify any listeners about the status of the supplied transferFunds.
//     *
//     * @param transferEntity An instance of {@link TransferEntity}.
//     * @param requiredStatus A {@link Status} that the supplied {@code transferEntity} must be in for any notifications
//     *                       to be published.
//     */
//    @VisibleForTesting
//    protected void notify(final LedgerTransferEvent ledgerTransferEvent) {
//
//        Objects.requireNonNull(ledgerTransferEvent);
//
//
//        // Notify for the Debit Account
//        {
//                ledgerEventPublisher.publish(ledgerTransferEvent);
//
//        }
//        // Notify for the Credit Account
//        {
//            final LedgerAccountId ledgerAccountId = transferEntity.getCreditAccountId() == null ? null :
//                    LedgerAccountId.of(transferEntity.getCreditAccountId());
//            if (ledgerAccountId != null) {
//                ledgerEventPublisher.publish(
//                        toLedgerTransferCreatedEvent(ledgerAccountId, transferEntity));
//            }
//        }
//
//    }

    @VisibleForTesting
    protected LedgerTransferCreatedEvent toLedgerTransferCreatedEvent(
            final LedgerAccountId ledgerAccountId, final TransferEntity savedTransferEntity
    ) {
        requireNonNull(savedTransferEntity);

        // TODO
        final Object ilpPacketHeader;
        final LedgerTransferCreatedEvent event = new LedgerTransferCreatedEvent(
                LedgerTransferId.of(savedTransferEntity.getId()),
                this.ledgerInfo,
                // TODO: Grab this, but from where?
                //ilpPacketHeader,
                null,
                ledgerAccountId,
                savedTransferEntity.getAmount()
        );

        return event;
    }

    @VisibleForTesting
    protected LedgerTransferUpdatedEvent toLedgerTransferUpdatedEvent(
            final LedgerAccountId ledgerAccountId, final TransferEntity savedTransferEntity
    ) {
        requireNonNull(savedTransferEntity);

        // TODO
        final Object ilpPacketHeader;
        final LedgerTransferUpdatedEvent event = new LedgerTransferUpdatedEvent(
                LedgerTransferId.of(savedTransferEntity.getId()),
                this.ledgerInfo,
                // TODO: Grab this, but from where?
                //ilpPacketHeader,
                null,
                ledgerAccountId,
                savedTransferEntity.getAmount()
        );

        return event;
    }

//    @VisibleForTesting
//    protected LedgerTransferUpdatedEvent toLedgerTransferUpdatedEvent(
//            final LedgerAccountId ledgerAccountId, final TransferEntity savedTransferEntity
//    ) {
//        requireNonNull(savedTransferEntity);
//
//        // TODO
//        final Object ilpPacketHeader;
//        final LedgerTransferUpdatedEvent event = new LedgerTransferUpdatedEvent(
//                LedgerTransferId.of(savedTransferEntity.getId()),
//                this.ledgerInfo,
//                // TODO: Grab this, but from where?
//                //ilpPacketHeader,
//                null,
//                ledgerAccountId,
//                savedTransferEntity.getAmount()
//        );
//
//        return event;
//    }
}
