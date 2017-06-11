package money.fluid.ilp.ledger.web.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import money.fluid.ilp.ledger.exceptions.problems.transfers.TransferNotFoundProblem;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import money.fluid.ilp.ledger.services.LedgerTransferService;
import money.fluid.ilp.ledger.services.factories.TransferInputFactory;
import money.fluid.ilp.ledger.web.factories.TransferRepresentationFactory;
import money.fluid.ilp.ledger.web.model.TransferInputRepresentation;
import money.fluid.ilp.ledger.web.model.TransferRepresentation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST endpoint for handling interactions with a {@link TransferRepresentation} resource located under the URL path
 * of "/transfers".
 * <p>
 * A transferFunds represents money being moved around within a single ledger.  As such, a transferFunds debits one account and
 * credits another account for the exact same amount.
 * <p>
 * A transferFunds can be conditional upon a supplied Crypto-Condition, in which case it executes automatically when
 * presented with the fulfillment for the condition (Assuming the transferFunds has not expired or been canceled first).  If
 * no crypto-condition is specified, the transferFunds is unconditional, and executes as soon as it is prepared.
 *
 * @see "RFC-12"
 */
@RestController
@RequiredArgsConstructor
public class TransferController {

    private static final String TRANSFER_ID = "transfer_id";

    @NonNull
    private final TransferRepresentationFactory transferRepresentationFactory;

    @NonNull
    private final TransferInputFactory transferInputFactory;

    @NonNull
    private final LedgerTransferService ledgerTransferService;

    /**
     * Create a new Transfer resource using the optionally supplied inputs.
     *
     * @return An instance of {@link TransferRepresentation}
     */
    @RequestMapping(
            path = "/transfers/{" + TRANSFER_ID + "}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public TransferRepresentation getTransfer(@PathVariable(TRANSFER_ID) final String transferId) {
        final Transfer transfer = this.ledgerTransferService.getTransfer(
                LedgerTransferId.of(transferId)).orElseThrow(() -> new TransferNotFoundProblem(
                LedgerTransferId.of(transferId)));
        return this.transferRepresentationFactory.construct(transfer);
    }

    /**
     * Create a new Transfer resource using the optionally supplied inputs.
     *
     * @return An instance of {@link TransferRepresentation}
     */
    @RequestMapping(
            path = "/transfers/{" + TRANSFER_ID + "}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public TransferRepresentation updateTransfer(
            @PathVariable(TRANSFER_ID) final String transferId,
            @RequestBody final TransferInputRepresentation transferInputRepresentation
    ) {
        final TransferInput transferInput = this.transferInputFactory.convert(transferInputRepresentation);
        final Transfer transfer = this.ledgerTransferService.updateTransfer(
                LedgerTransferId.of(transferId), transferInput);
        return this.transferRepresentationFactory.construct(transfer);
    }
}
