package money.fluid.ilp.ledger.services.transfer;

import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.exceptions.problems.transfers.InvalidTransferIdentifierProblem;
import money.fluid.ilp.ledger.exceptions.problems.transfers.InvalidTransferStatusProblem;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

/**
 * A helper class to validate transferFunds data for create/update operations.
 */
@Service
@RequiredArgsConstructor
@ToString
public class TransferValidator {

    /**
     * Validate the contents of the incoming {@link TransferInput}.
     *
     * @param transferInput
     */
    public void validateTransferInputForCreate(final TransferInput transferInput) {
        requireNonNull(transferInput);
        transferInput.getId().ifPresent(this::validateClientId);
        // TODO: Throw problems instead of RuntimeException...
        transferInput.getCreditAccountId().orElseThrow(() -> new RuntimeException("Credit account is required!"));
        transferInput.getDebitAccountId().orElseThrow(() -> new RuntimeException("Debit account is required!"));
    }

    /**
     * Validate that an incoming {@link TransferInput} qualifies as an optimistic transferFunds that can be executed
     * immediately.  To qualify, a transferFunds must meet the following requirements:
     * <p>
     * <pre>
     * <ul>
     *  <li>The id must be non-blank.</li>
     *  <li>The debit and credit accounts must be non-blank.</li>
     *  <li>The fulfillment must be absent.</li>
     * </ul>
     * </pre>
     *
     * @param transferInput
     */
    public boolean isOptimisticTransfer(final TransferInput transferInput) {
        requireNonNull(transferInput);

        return Optional.ofNullable(transferInput)
                .filter(tr -> tr.getId().isPresent())
                .filter(tr -> !StringUtils.isBlank(tr.getId().toString()))
                .filter(tr -> !tr.getExecutionCondition().isPresent())
                .filter(tr -> tr.getDebitAccountId().isPresent())
                .filter(tr -> !StringUtils.isBlank(tr.getDebitAccountId().get().value()))
                .filter(tr -> tr.getCreditAccountId().isPresent())
                .filter(tr -> !StringUtils.isBlank(tr.getCreditAccountId().get().value()))
                .map(tr -> true)
                .orElse(false);
    }

    /**
     * Validate that the client-supplied {@link LedgerTransferId} conforms to the following rules:
     * <p>
     * <pre>
     *     <ol>
     *         <li>Is globally unique.</li>
     *         <li>Is non-guessable.</li>
     *         <li>Unique across the ledger</li>
     *     </ol>
     * </pre>
     *
     * @param clientSuppliedLedgerTransferId
     */
    @VisibleForTesting
    void validateClientId(final LedgerTransferId clientSuppliedLedgerTransferId) {
        // TODO: Do this.  See discussion in https://github.com/interledger/rfcs/pull/125
        try {
            UUID.fromString(clientSuppliedLedgerTransferId.value());
        } catch (Exception e) {
            throw new InvalidTransferIdentifierProblem(clientSuppliedLedgerTransferId);
        }
    }

//    public void validateTransferUpdate(final Transfer originalTransfer, final Transfer newTransfer) {
//
//        requireNonNull(originalTransfer);
//        requireNonNull(newTransfer);
//
//        switch (newTransfer.getStatus()) {
//            case CREATED: {
//                Preconditions.checkArgument(originalTransfer.getStatus().equals(Status.PROPOSED));
//                break;
//            }
//            case EXECUTED: {
//                Preconditions.checkArgument(originalTransfer.getStatus().equals(Status.CREATED));
//                // TODO: Validate non-optimistic mode.
//                break;
//            }
//            case REJECTED: {
//                Preconditions.checkArgument(originalTransfer.getStatus().equals(Status.CREATED));
//                // TODO: Validate non-optimistic mode.
//                break;
//            }
//            default: {
//                throw new RuntimeException("Unhandled new status " + newTransfer.getStatus());
//            }
//        }
//
//    }

    /**
     * Validates an incoming {@link TransferInput} to ensure that an outside caller isn't updating something they
     * shouldn't be updating.
     *
     * @param incomingTransfer
     */
    public void validateTransferInputForUpdate(final Transfer currentTransfer, final TransferInput incomingTransfer) {
        requireNonNull(incomingTransfer);

        // TODO: Finish this method.
        // TODO: Use Hibernate Validator

//        if (currentTransfer.getStatus().equals(incomingTransfer.getStatus())) {
//            return;
//        }

        ///////////////////////
        // Status validations...
        ///////////////////////
        switch (currentTransfer.getStatus()) {
            case PREPARED: {
                // External clients may not adjust the status, and can only update a Transfer that is in the CREATED state (i.e., has not been executed or rejected).  For example, a Transfer missing required info like account or amount info.
                return;
            }
            case EXECUTED:
            case REJECTED:
            default:
                throw new InvalidTransferStatusProblem(currentTransfer.getId(), currentTransfer.getStatus());
        }
    }
}
