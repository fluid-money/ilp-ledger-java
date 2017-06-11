package org.ilpx.ledger.core;

import money.fluid.ilp.ledger.exceptions.InsufficientFundsException;
import money.fluid.ilp.ledger.exceptions.TransferNotConditionalException;
import money.fluid.ilp.ledger.exceptions.TransferStateException;
import money.fluid.ilp.ledger.exceptions.UnmetConditionException;
import money.fluid.ilp.ledger.exceptions.UnsupportedCryptoConditionException;
import money.fluid.ilp.ledger.exceptions.problems.transfers.TransferNotFoundProblem;
import money.fluid.ilp.ledger.model.LedgerInfo;
import money.fluid.ilp.ledger.model.Metadata;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import org.interledger.cryptoconditions.Fulfillment;

import java.util.Optional;

/**
 * The Common Ledger API is a RESTful API served by a ledger (or an adapter), which provides functionality necessary for
 * ILP compatibility. The Common Ledger API provides a single standard API that a ledger can serve in order to ease
 * integration with other Interledger Protocol components and applications, such as the reference ILP Client and ILP
 * Connector. This is not the only way a ledger can become ILP-enabled, but it provides a template that minimizes the
 * integration work necessary for compatibility with ILP software.
 *
 * @see "https://github.com/interledger/rfcs/blob/7a6a4b0723c347759c0366836c48c546efc9f268/0012-common-ledger-api/0012-common-ledger-api.md"
 */
public interface Ledger {

    /**
     * Retrieve some meta-data about the ledger.
     *
     * @return <code>LedgerInfo</code>
     */
    LedgerInfo getLedgerInfo();


//    /**
//     * Retrieve some meta-data about the ledger.
//     *
//     * @return An instance of {@link MetadataRepresentation}.
//     */
//    Metadata getMetaData();
//
//    /**
//     * Prepare a new transferFunds (conditional or unconditional) in the ledger.  When a transferFunds becomes prepared, it
//     * executes immediately if there is no condition. If you specify a {@link Transfer#getExecutionCondition()},
//     * then the funds are held until the beneficiary submits the matching {@link Fulfillment} or the {@link
//     * Transfer#getExpiresAt()} date/time is reached.
//     *
//     * @param transferFunds An instance of {@link Transfer} that contains all information required by the ledger to prepare
//     *                 an ILP transferFunds.
//     * @return An instance of {@link Transfer} as stored by the {@link money.fluid.ilp.ledger.model.Ledger}.
//     * @throws InsufficientFundsException          When {@link Transfer#getDebitAccountId()} would go below its minimum
//     *                                             allowed balance if this transferFunds were executed.
//     * @throws UnsupportedCryptoConditionException When a {@link Transfer} contains an {@link Transfer#getExecutionCondition()}
//     *                                             whose feature bitstring requires functionality not implemented by
//     *                                             this ledger.
//     */
//    Transfer prepareTransfer(
//            Transfer transferFunds
//    ) throws InsufficientFundsException, UnsupportedCryptoConditionException;
//
//    /**
//     * An accessor for a {@link Transfer} that enables a caller to check the details or status of a local
//     * transferFunds.
//     *
//     * @param transferId An instance of {@link TransferId} that uniquely identifie the {@link Transfer} to return.
//     * @return An instance of {@link Transfer} as stored by the {@link money.fluid.ilp.ledger.model.Ledger}.
//     */
//    Optional<Transfer> getTransfer(TransferId transferId);
//
//    /**
//     * Retrieve the associated {@link Fulfillment} for a transferFunds that has been executed or canceled.      *
//     *
//     * @param transferId An instance of {@link TransferId} that uniquely identifie the {@link Transfer} to return.
//     * @return An instance of {@link Transfer} as stored by the {@link money.fluid.ilp.ledger.model.Ledger}.
//     * @throws TransferNotFoundProblem If the associated {@link Transfer} is not found.
//     */
//    Optional<Fulfillment> getTransferFulfillment(TransferId transferId) throws TransferNotFoundProblem;

    //    /**
//     * Attempt to execute a transferFunds by submitting a Crypto-Condition {@link Fulfillment}. To execute a transferFunds, the
//     * transferFunds MUST begin in the prepared state and the submitted fulfillment must satisfy the Crypto-Condition in
//     * {@link Transfer#getExecutionCondition()}. Doing so transitions the transaction to {@link Status#EXECUTED}.
//     *
//     * @param transferId  A instance of {@link TransferId} used to uniquely identify the transferFunds.
//     * @param fulfillment An instance of {@link Fulfillment}.
//     * @return The submitted {@link Fulfillment} if no errors occurred.
//     * @throws TransferNotFoundException       If the associated {@link Transfer} is not found.
//     * @throws TransferNotConditionalException when a {@link Transfer} The transferFunds had no {@link Condition} to
//     *                                         fulfill.
//     * @throws UnmetConditionException         when a {@link Fulfillment} does not match the corresponding {@link
//     *                                         Transfer#getExecutionCondition()}.
//     * @throws TransferStateException          when {@link Transfer} in not in the {@link Status#CREATED} state when
//     *                                         the request was received. This occurs if the transferFunds has already been
//     *                                         executed, rejected, or expired.
//     */
//    Fulfillment submitFulfillment(
//            TransferId transferId, Fulfillment fulfillment
//    ) throws TransferNotFoundProblem, TransferNotConditionalException, UnmetConditionException, TransferStateException;


//    /**
//     * Reject a prepared {@link Transfer}. A transferFunds can only be rejected if it is in the prepared
//     * state.  Doing so transitions the transferFunds to the {@link Status#REJECTED} state.
//     *
//     * @param transferId A instance of {@link TransferId} used to uniquely identify the transferFunds.
//     * @param reason     An instance of {@link RejectionReason}.
//     * @throws TransferNotFoundException If the associated {@link Transfer} is not found.
//     */
//    Transfer rejectTransfer(TransferId transferId, RejectionReason reason) throws TransferNotFoundException;
//
//    /**
//     * Get the account indicated by the supplied account identifier.
//     * <p>
//     * Authorization Note: The owner of an account MUST be able to get the account resource. A ledger MAY allow other
//     * account owners or unauthorized users to get other account resources. A ledger MAY return a subset of fields when
//     * it returns an account resource to any client other than the account owner (for example, omitting the balance).
//     *
//     * @param accountId An instance of {@link AccountId} that uniquely identifies the account to retrieve.
//     * @return An instance of {@link Account}.
//     */
//    Account getAccount(AccountId accountId);
//
//    /**
//     * Try to send a notification to another account on this {@link Ledger}. The message is only delivered if {@link
//     * Message#getTo()} is subscribed to account notifications.  For example, ILP Connectors use this method
//     * to share quote information.
//     *
//     * @param message An instance of {@link Message}.
//     */
//    void sendMessage(Message message);

    // TODO: See https://github.com/interledger/rfcs/blob/7a6a4b0723c347759c0366836c48c546efc9f268/0012-common-ledger-api/0012-common-ledger-api.md#message-resource or common-ledger-api-0012.
    // getAuthToken();
    // WebSockets

    // Accessors for managers that a Ledger must support.

//    LedgerConnectionManager getLedgerConnectionManager();
//
//    LedgerAccountManager getLedgerAccountManager();

    // TODO: Determine if the Ledger (a noun) should actually have operations on it, or if it would be preferable to have
    // a Ledger and a LedgerService that can possibly allow for operations with a Ledger.

//    /**
//     * Initiates a ledger-local transferFunds.
//     *
//     * @param transferFunds <code>LedgerTransfer</code>
//     */
//    void send(LedgerTransfer transferFunds);
//
//    /**
//     * Reject a transferFunds
//     * <p>
//     * This should only be allowed if the entity rejecting the transferFunds is the
//     * receiver
//     *
//     * @param reason
//     */
//    void rejectTransfer(IlpTransactionId ilpTransactionId, LedgerTransferRejectedReason reason);
//
//    /**
//     * Submit a fulfillment to a ledger.
//     * <p>
//     * The ledger will execute all transfers that are fulfilled by this
//     * fulfillment.
//     *
//     * @param ilpTransactionId
//     * @param fulfillment      the fulfillment for this transferFunds
//     */
//    void fulfillCondition(IlpTransactionId ilpTransactionId, Fulfillment fulfillment);
//
//    /**
//     * Submit an optimistic-mode fulfillment to a ledger.
//     * <p>
//     * The ledger will execute all transfers for the specified {@link IlpTransactionId} since the ILP transaction is an
//     * optimistic-mode transferFunds, and no fulfillment is required.
//     */
//    //void fulfillCondition(IlpTransactionId ilpTransactionId);
//
//    // Accessors for managers that a Ledger must support.
//
//    LedgerConnectionManager getLedgerConnectionManager();
//
//    LedgerAccountManager getLedgerAccountManager();

}
