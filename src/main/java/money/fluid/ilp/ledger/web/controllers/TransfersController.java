package money.fluid.ilp.ledger.web.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.TransferInput;
import money.fluid.ilp.ledger.services.LedgerTransferService;
import money.fluid.ilp.ledger.services.factories.TransferFactory;
import money.fluid.ilp.ledger.services.factories.TransferInputFactory;
import money.fluid.ilp.ledger.web.factories.TransferRepresentationFactory;
import money.fluid.ilp.ledger.web.model.TransferInputRepresentation;
import money.fluid.ilp.ledger.web.model.TransferRepresentation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

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
public class TransfersController {

    @NonNull
    private final TransferFactory transferFactory;

    @NonNull
    private final TransferInputFactory transferInputFactory;

    @NonNull
    private final TransferRepresentationFactory transferRepresentationFactory;

    @NonNull
    private final LedgerTransferService ledgerLedgerTransferService;

//    /**
//     * Create a new Transfer resource using the optionally supplied inputs.
//     *
//     * @return An instance of {@link TransferRepresentation}
//     */
//    @RequestMapping(
//            path = "/transfers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    @ResponseBody
//    public TransferRepresentation createTransfer() {
//        final Transfer transferFunds = this.ledgerLedgerTransferService.createTransfer();
//        return this.transferRepresentationFactory.construct(transferFunds);
//    }

    /**
     * Create a new Transfer resource using the optionally supplied inputs.
     *
     * @return An instance of {@link TransferRepresentation}
     */
    @RequestMapping(
            path = "/transfers", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public TransferRepresentation createTransferWithPayload(
            @RequestBody final TransferInputRepresentation transferInputRepresentation
    ) {
        Objects.requireNonNull(transferInputRepresentation);

        // Transform to the internal format...
        final TransferInput transferInput = this.transferInputFactory.convert(transferInputRepresentation);

        // Delegate to the Service to Create the Transfer...
        final Transfer transfer = this.ledgerLedgerTransferService.createTransfer(transferInput);

        // Transform the response to the external format...
        return this.transferRepresentationFactory.construct(transfer);
    }
}
