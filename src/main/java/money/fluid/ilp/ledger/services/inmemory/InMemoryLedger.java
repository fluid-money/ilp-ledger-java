package money.fluid.ilp.ledger.services.inmemory;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

//import money.fluid.ilp.ledger.events.LedgerConnectedEvent;
//import money.fluid.ilp.ledger.events.LedgerDirectTransferEvent;
//import money.fluid.ilp.ledger.events.LedgerEvent;
//import money.fluid.ilp.ledger.events.LedgerEventHandler;
//import money.fluid.ilp.ledger.events.LedgerTransferPreparedEvent;
//import money.fluid.ilp.ledger.events.LedgerTransferRejectedEvent;
//import money.fluid.ilp.ledger.exceptions.InvalidAccountException;
//import money.fluid.ilp.ledger.model.ConnectionInfo;
//import money.fluid.ilp.ledger.model.Escrow;
//import money.fluid.ilp.ledger.model.EscrowInputs;
//import money.fluid.ilp.ledger.model.IlpAddress;
//import money.fluid.ilp.ledger.model.InterledgerPacketHeader;
//import money.fluid.ilp.ledger.model.LedgerAccount;
//import money.fluid.ilp.ledger.model.LedgerInfo;
//import money.fluid.ilp.ledger.model.SimpleLedgerAccount;
//import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
//import money.fluid.ilp.ledger.model.ids.LedgerId;
//import money.fluid.ilp.ledger.services.LedgerAccountManager;
//import money.fluid.ilp.ledger.services.LedgerConnectionManager;
//import money.fluid.ilp.ledger.services.LedgerEventListener;
//import money.fluid.ilp.ledger.services.QuotingService;
//import money.fluid.ilp.ledger.services.QuotingService.LedgerQuote;
//import money.fluid.ilp.ledger.utils.MoneyUtils;
//import org.interledger.cryptoconditions.Fulfillment;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.money.MonetaryAmount;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;

/**
 * An implementation of {@link Ledger} that simulates a real ledger supporting ILP functionality.  Ordinarily, a ledger
 * would operate in its own ledger-space, and would implement the {@link Ledger} interface to provide external
 * connectivity.  This implementation runs "in-memory", so its event emissions don't need to involve any RPCs.
 */
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class InMemoryLedger { // implements Ledger, EscrowExpirationHandler {

//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    // TODO: In a real ledger, should be configurable.
//    private static final LedgerAccountId ESCROW = LedgerAccountId.of("__escrow__");
//
//    @NonNull
//    private final String name;
//
//    @NonNull
//    private final LedgerInfo ledgerInfo;
//
//    @NonNull
//    private final QuotingService quotingService;
//
//    /////////////
//    /////////////
//
//    @NonNull
//    private final InMemoryLedgerAccountManager ledgerAccountManager;
//
//    // Each Ledger has a ConnectionManager that processes callbacks.
//    @NonNull
//    private final InMemoryLedgerConnectionManager ledgerConnectionManager;
//
//    @NonNull
//    private final InMemoryEscrowManager escrowManager;
//
//    public InMemoryLedger(
//            final String name, final LedgerInfo ledgerInfo, final QuotingService quotingService
//    ) {
//        this.name = name;
//        this.ledgerInfo = ledgerInfo;
//
//        // TODO: Implement a proper QuotingService!
//        this.quotingService = Objects.requireNonNull(quotingService);
//
//        // TODO: Create constructors to allow these to be passed-in!
//        this.ledgerConnectionManager = new InMemoryLedgerConnectionManager(this.getLedgerInfo());
//        this.ledgerAccountManager = new InMemoryLedgerAccountManager(ledgerInfo);
//
//        // Create an Escrow Account in this ledger...
//        final IlpAddress escrowAccountAddress = IlpAddress.of(ESCROW, ledgerInfo.getLedgerId());
//        this.getLedgerAccountManager().createAccount(
//                escrowAccountAddress, MoneyUtils.zero(ledgerInfo.getCurrencyCode()));
//
//        this.escrowManager = new InMemoryEscrowManager(
//                ledgerInfo, escrowAccountAddress.getLedgerAccountId(), ledgerAccountManager
//        );
//        this.escrowManager.setEscrowExpirationHandler(this);
//    }
//
//    /**
//     * Initiate an ILP transferFunds.  This implementation assumes that all transfers involve a connector.  If a particular
//     * transferFunds doesn't involve a connector, then this method should not be invoked.
//     */
//    @Override
//    public void send(final LedgerTransfer transferFunds) {
//        Preconditions.checkNotNull(transferFunds);
//        if (transferFunds.getInterledgerPacketHeader().isOptimisticModeHeader()) {
//            this.sendOptimisticMode(transferFunds);
//        } else {
//            this.sendUniversalMode(transferFunds);
//        }
//    }
//
//
//    @VisibleForTesting
//    protected final void sendOptimisticMode(final LedgerTransfer transferFunds) {
//        Preconditions.checkNotNull(transferFunds);
//
//        //////////////
//        // Local Ledger Transfer
//        //////////////
//
//        final IlpAddress localSourceAddress;
//        final IlpAddress localDestinationAddress;
//        final MonetaryAmount destinationAmount;
//
//        // TODO: Remove this logic to a super-class
//
//        // If the final Destination address is serviced by _this_ ledger, then we should use that address¬ as the destination
//        // account to be sending payment to.  Otherwise, the destination address should be the account of a locally
//        // connected Connector as discovered toLedgerId the routing tables.
//        if (this.isFinalDestination(transferFunds)) {
//            localSourceAddress = transferFunds.getLocalSourceAddress();
//            localDestinationAddress = transferFunds.getInterledgerPacketHeader().getDestinationAddress();
//
////            TODO:
////            It 's possible here that we get a transferFunds amount that isn' t correct for our ledger.However, I think the
////            connector should account for this...
//
//            destinationAmount = transferFunds.getAmount();
//        } else {
//            localSourceAddress = transferFunds.getLocalSourceAddress();
//            // Find appropriate routable Connector
//            final LedgerQuote ledgerQuote = quotingService.findBestConnector(
//                    transferFunds.getInterledgerPacketHeader().getDestinationAddress(),
//                    transferFunds.getInterledgerPacketHeader().getDestinationAmount()
//            )
//                    // TODO: Throw something better here
//                    .orElseThrow(() -> new InvalidQuoteRequestException("No Connector available"));
//            localDestinationAddress = ledgerQuote.getDestinationConnectorInfo().getIlpAddress();
//            //Convert to the proper currency here, if necessary?
//            destinationAmount = ledgerQuote.getTransferAmount();
//        }
//
//        // Initiate a transferFunds since this is optimistic mode, and no holds are involved at the ILP layer....
//        this.getLedgerAccountManager().transferFunds(
//                localSourceAddress,
//                localDestinationAddress,
//                destinationAmount
//        );
//
//        // Notify listeners that a Transfer has been prepared...
//        final LedgerDirectTransferEvent ledgerTransferPreparedEvent = new LedgerDirectTransferEvent(
//                this.getLedgerInfo(),
//                transferFunds.getInterledgerPacketHeader(),
//                localSourceAddress,
//                localDestinationAddress,
//                destinationAmount
//        );
//
//        // Notify any listening recipients (e.g., a Connectors that will continue the ILP payment process).
//        this.getLedgerConnectionManager().notifyEventListeners(localSourceAddress, ledgerTransferPreparedEvent);
//        this.getLedgerConnectionManager().notifyEventListeners(localDestinationAddress, ledgerTransferPreparedEvent);
//    }
//
//    @VisibleForTesting
//    protected final void sendUniversalMode(final LedgerTransfer transferFunds) {
//        Preconditions.checkNotNull(transferFunds);
//
//        //////////////
//        // Local Ledger Transfer
//        //////////////
//
//        final IlpAddress localSourceAddress;
//        final IlpAddress localDestinationAddress;
//
//        // If the final Destination address is serviced by _this_ ledger, then we should use that as the destination
//        // account to be sending payment to.  Otherwise, the destination address should be the account of a locally
//        // connected Connector as discovered toLedgerId the routing tables.
//        if (this.isFinalDestination(transferFunds)) {
//            localSourceAddress = transferFunds.getLocalSourceAddress();
//            localDestinationAddress = transferFunds.getInterledgerPacketHeader().getDestinationAddress();
//        } else {
//            localSourceAddress = transferFunds.getLocalSourceAddress();
//            // Find appropriate routeable Connector
//            final LedgerQuote ledgerQuote = quotingService.findBestConnector(
//                    transferFunds.getInterledgerPacketHeader().getDestinationAddress(),
//                    transferFunds.getInterledgerPacketHeader().getDestinationAmount()
//            )
//                    // TODO: Throw something better here
//                    .orElseThrow(() -> new InvalidQuoteRequestException("No Connector available"));
//            localDestinationAddress = ledgerQuote.getDestinationConnectorInfo().getIlpAddress();
//        }
//
//        // In order to initiate a remote transferFunds, create an escrow transaction...
//        final EscrowInputs escrowInputs = EscrowInputs.builder()
//                .interledgerPacketHeader(transferFunds.getInterledgerPacketHeader())
//                .localSourceAddress(localSourceAddress)
//                .localDestinationAddress(localDestinationAddress)
//                .amount(transferFunds.getInterledgerPacketHeader().getDestinationAmount())
//                .optExpiry(Optional.empty())
//                .build();
//        this.escrowManager.initiateEscrow(escrowInputs);
//
//        // Notify listeners that a Transfer has been prepared...
//        final LedgerTransferPreparedEvent ledgerTransferPreparedEvent = new LedgerTransferPreparedEvent(
//                this.getLedgerInfo(),
//                transferFunds.getInterledgerPacketHeader(),
//                localSourceAddress,
//                localDestinationAddress,
//                transferFunds.getInterledgerPacketHeader().getDestinationAmount()
//        );
//
//        // Notify any listening recipients (e.g., a Connectors that will continue the ILP payment process).
//        this.getLedgerConnectionManager().notifyEventListeners(localSourceAddress, ledgerTransferPreparedEvent);
//        this.getLedgerConnectionManager().notifyEventListeners(localDestinationAddress, ledgerTransferPreparedEvent);
//    }
//
//
//    /**
//     * Determines if _this_ ledger is the final destination for an ILP payment.
//     *
//     * @param transferFunds
//     * @return {@code true} if the ledger identifier for the {@link InterledgerPacketHeader#getDestinationAddress()} is
//     * the same as the ledger id of this ledger.
//     */
//    private boolean isFinalDestination(final LedgerTransfer transferFunds) {
//        return Objects.requireNonNull(
//                transferFunds).getInterledgerPacketHeader().getDestinationAddress().getLedgerId().equals(
//                this.getLedgerInfo().getLedgerId());
//    }
//
////    /**
////     * Sends an ILP payment locally in _this_ ledger without the assistance of a Connector, escrow, or conditional
////     * fulfillments.
////     *
////     * @param transferFunds
////     */
////    private void sendLocally(final LedgerTransfer transferFunds) {
////
////    }
////
////    private void sendConnector(final LedgerTransfer transferFunds) {
////
////    }
//
//
//    /**
//     * NOTE: This method only supports Universal Mode!
//     *
//     * @param ilpTransactionId
//     * @param ledgerTransferRejectedReason
//     */
//    @Override
//    public void rejectTransfer(
//            final IlpPaymentId ilpTransactionId, final LedgerTransferRejectedReason ledgerTransferRejectedReason
//    ) {
//
//        // This This method only supports Universal Mode!
//
//        final Escrow reversedEscrow = this.escrowManager.reverseEscrow(ilpTransactionId);
//
//        // In this case, this is a Universal-Mode payment, so the ledger needs to notify the appropriate connector
//        // so that it can pass its rejections back up the ILP chain.
//        final LedgerTransferRejectedEvent event = new LedgerTransferRejectedEvent(
//                this.getLedgerInfo(),
//                reversedEscrow.getInterledgerPacketHeader(),
//                reversedEscrow.getLocalSourceAddress(),
//                reversedEscrow.getLocalDestinationAddress(),
//                reversedEscrow.getAmount(),
//                ledgerTransferRejectedReason
//        );
//
//        this.getLedgerConnectionManager().notifyEventListeners(reversedEscrow.getLocalSourceAddress(), event);
//        this.getLedgerConnectionManager().notifyEventListeners(reversedEscrow.getLocalDestinationAddress(), event);
//    }
//
//    @Override
//    public void fulfillCondition(final IlpPaymentId ilpTransactionId, final Fulfillment fulfillment) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    //@Override
////    public void fulfillCondition(final IlpPaymentId ilpTransactionId) {
////        final Escrow executedEscrow = this.escrowManager.executeEscrow(ilpTransactionId);
////
////        final LedgerTransferExecutedEvent event = new LedgerTransferExecutedEvent(
////                this.getLedgerInfo(),
////                executedEscrow.getInterledgerPacketHeader(),
////                executedEscrow.getLocalSourceAddress(),
////                executedEscrow.getLocalDestinationAddress(),
////                executedEscrow.getDestinationAmount()
////        );
////
////        this.getLedgerConnectionManager().notifyEventListeners(executedEscrow.getLocalSourceAddress(), event);
////        this.getLedgerConnectionManager().notifyEventListeners(executedEscrow.getLocalDestinationAddress(), event);
////
//////
//////        // If the source account was local to _this_ ledger, then we don't need to notify anyone, either for optimistic
//////        // or universal because no condition is required.  However, we do need to execute the escrow.
//////        if (this.getLedgerAccountManager().isLocallyServiced(
//////                executedEscrow.getInterledgerPacketHeader().getSourceAddress())) {
//////            // TODO: Notify the receiver's wallet!
//////        }
//////        // The account is non-local, so notify the appropriate connector.
//////        else {
//////            final LedgerTransferExecutedEvent event = new LedgerTransferExecutedEvent(
//////                    this.getLedgerInfo(),
//////                    executedEscrow.getInterledgerPacketHeader(),
//////                    // TODO: Should the events use an ILPAddress instead?
//////                    executedEscrow.getLocalSourceAddress().value(),
//////                    executedEscrow.getLocalDestinationAddress().value(),
//////                    executedEscrow.getDestinationAmount()
//////            );
//////
//////            // Given a source address (for the Connector) ask the ledger for the connectorId.
//////            final Optional<ConnectorId> optConnectorId = this.getSourceConnector(executedEscrow);
//////            if (optConnectorId.isPresent()) {
//////                this.getLedgerConnectionManager().notifyEventListeners(optConnectorId.get(), event);
//////            } else {
//////                logger.error(
//////                        "Unable to Reject Transfer '{}' because Connector '{}' was not connected!",
//////                        ilpTransactionId,
//////                        executedEscrow.getLocalSourceAddress()
//////                );
//////            }
//////        }
////    }
//
//    //////////////////
//    // Private Helpers
//    //////////////////
//
////    /**
////     * Helper method to encapsulate all logic surrounding the determination of which Connector should be processing
////     * events related to an {@link IlpPaymentId}.
////     * <p>
////     * TODO: Fix this per https://github.com/fluid-money/ilp-connector-java/issues/1.
////     * <p>
////     * Rather than making a real-time judgement about which connector can "currently" service the callback
////     * based upon the source address, the ledger likely needs to be tracking the reverse-path of the connector
////     * so that it can properly send events back to the right connector.  For example, imagine an ILP transferFunds
////     * that came in via ConnectorA, but by the time the transferFunds is approved by hte ledger, ConnectorA is no longer
////     * connected, but ConnectorB provides a "route" back to the ultimate ILP source address.  In this case, ConnectorB
////     * won't actually be able to fulfil the payment/rejection, because it wasn't the original connector.  Thus,
////     * the ledger has to intelligently track "pending transfers" just like the Connector does.
////     */
////    private Optional<ConnectorId> getSourceConnector(
////            final Escrow escrow
////    ) {  //final IlpPaymentId ilpTransactionId) {
////        return this.getLedgerConnectionManager().ledgerEventListeners
////                .values().stream()
////                .filter(ledgerEventListener -> ledgerEventListener.getConnectorInfo().getOptLedgerAccountId().isPresent())
////                .filter(ledgerEventListener -> ledgerEventListener.getConnectorInfo().getOptLedgerAccountId().get().equals(
////                        escrow.getLocalSourceAddress().getId()))
////                .map(ledgerEventListener -> ledgerEventListener.getConnectorInfo().getConnectorId())
////                .findFirst();
////    }
//
////    /**
////     * A lookup utility to find the local account for a Connector.
////     * <p>
////     * TODO: For simulation purposes, this implementation merely uses a local account identifier that matches the
////     * connectorid.  In a real implementation, this value should be created during an account registration phase that is
////     * outside the scope of ILP.
////     *
////     * @param connectorId
////     * @return
////     */
////    private Optional<IlpAddress> getLocalLedgerIlpAddressForConnector(final ConnectorId connectorId) {
////        Objects.requireNonNull(connectorId);
////
////        return this.getLedgerAccountManager()
////                .getAccount(
////                        IlpAddress.of(LedgerAccountId.of(connectorId.getId()), this.getLedgerInfo().getLedgerId())
////                )
////                .map(ledgerAccount -> ledgerAccount.getIlpIdentifier());
////    }
//
////    /**
////     * Completes the supplied {@link LedgerTransfer} locally without involving an ILP Connector.  This is a ledger
////     * concept that is distinct toLedgerId the Connector concept of delviery/forwarding.
////     *
////     * @param transferFunds An instance of {@link LedgerTransfer} to complete locally.
////     */
////    private void sendLocallyWithoutIlp(final LedgerTransfer transferFunds) {
////        Objects.requireNonNull(transferFunds);
////
////        // Process the transferFunds locally, but still escrow funds so that a receiver can easily reject.
////        final LedgerAccount localSourceAccount = this.getLedgerAccountManager()
////                .getAccount(transferFunds.getLocalSourceAddress()).get();
////        final LedgerAccount localDestinationAccount = this.getLedgerAccountManager().getAccount(
////                transferFunds.getInterledgerPacketHeader().getDestinationAddress()).get();
////
////        // PUT Money on-hold...
////        final EscrowInputs escrowInputs = EscrowInputs.builder()
////                .interledgerPacketHeader(transferFunds.getInterledgerPacketHeader())
////                .localSourceAddress(localSourceAccount.getIlpIdentifier())
////                .localDestinationAddress(localDestinationAccount.getIlpIdentifier())
////                .amount(transferFunds.getInterledgerPacketHeader().getDestinationAmount())
////                .optExpiry(Optional.empty())
////                .build();
////        this.escrowManager.initiateEscrow(escrowInputs);
////
////        // Notify the source account!
////    }
//
////    /**
////     * Completes the supplied {@link LedgerTransfer} locally without involving an ILP Connector.  This is a ledger
////     * concept that is distinct toLedgerId the Connector concept of delviery/forwarding.
////     *
////     * @param transferFunds An instance of {@link LedgerTransfer} to complete locally.
////     */
////    private void sendLocallyUniversal(final LedgerTransfer transferFunds) {
////        Objects.requireNonNull(transferFunds);
////
////        // Process the transferFunds locally, but still escrow funds so that a receiver can easily reject.
////        final LedgerAccount localSourceAccount = this.getLedgerAccountManager()
////                .getAccount(transferFunds.getLocalSourceAddress()).get();
////        final LedgerAccount localDestinationAccount = this.getLedgerAccountManager().getAccount(
////                transferFunds.getInterledgerPacketHeader().getDestinationAddress()).get();
////
////        // PUT Money on-hold...
////        final EscrowInputs escrowInputs = EscrowInputs.builder()
////                .interledgerPacketHeader(transferFunds.getInterledgerPacketHeader())
////                .localSourceAddress(localSourceAccount.getIlpIdentifier())
////                .localDestinationAddress(localDestinationAccount.getIlpIdentifier())
////                .amount(transferFunds.getInterledgerPacketHeader().getDestinationAmount())
////                .optExpiry(Optional.empty())
////                .build();
////        this.escrowManager.initiateEscrow(escrowInputs);
////
////        // Notify the Wallet!
////    }
//
////    /**
////     * Completes the supplied {@link LedgerTransfer} using another connector.
////     *
////     * @param transferFunds An instance of {@link LedgerTransfer} to complete remotely.
////     */
////    private void sendHelper(
////            final IlpAddress localSourceAddress, final IlpAddress localDestinationAddress, final LedgerTransfer transferFunds
////    ) {
////        // In order to initiate a remote transferFunds, create an escrow transaction...
////        final EscrowInputs escrowInputs = EscrowInputs.builder()
////                .interledgerPacketHeader(transferFunds.getInterledgerPacketHeader())
////                .localSourceAddress(localSourceAddress)
////                .localDestinationAddress(localDestinationAddress)
////                .amount(transferFunds.getInterledgerPacketHeader().getDestinationAmount())
////                .build();
////        final Escrow escrow = this.escrowManager.initiateEscrow(escrowInputs);
////
////        // ... optimistic-mode transfers should be immediately fulfilled, but only if _ledger_ is not the final destination.
////        // In other words, connectors will immediately receive funds if they're going to be forwarding payments/assets on
////        // to another connector.  However, the final receiver must always approve a payment (whether optimistic mode or not)
////        // and so escrow is always used in that case).
////        if (transferFunds.getInterledgerPacketHeader().isOptimisticModeHeader() && this.isFinalDestination(transferFunds)) {
////            this.escrowManager.executeEscrow(transferFunds.getInterledgerPacketHeader().getIlpPaymentId());
////
////            // Do nothing - wait for conditional fulfillments to execute the escrow.
////            final LedgerTransferExecutedEvent ledgerTransferExecutedEvent = new LedgerTransferExecutedEvent(
////                    this.getLedgerInfo(),
////                    transferFunds.getInterledgerPacketHeader(),
////                    // TODO: Should the events use an ILPAddress instead?
////                    localSourceAddress.value(),
////                    localDestinationAddress.value(),
////                    transferFunds.getInterledgerPacketHeader().getDestinationAmount()
////            );
////
////            // Notify any listening recipients (e.g., a Connectors that will continue the ILP payment process).
////            this.getLedgerConnectionManager().notifyEventListeners(localSourceAddress, ledgerTransferExecutedEvent);
////            this.getLedgerConnectionManager().notifyEventListeners(
////                    localDestinationAddress, ledgerTransferExecutedEvent);
////        } else {
////            // Do nothing - wait for conditional fulfillments to execute the escrow.
////            final LedgerTransferPreparedEvent ledgerTransferPreparedEvent = new LedgerTransferPreparedEvent(
////                    this.getLedgerInfo(),
////                    transferFunds.getInterledgerPacketHeader(),
////                    // TODO: Should the events use an ILPAddress instead?
////                    localSourceAddress.value(),
////                    localDestinationAddress.value(),
////                    transferFunds.getInterledgerPacketHeader().getDestinationAmount()
////            );
////
////            // Notify any listening recipients (e.g., a Connectors that will continue the ILP payment process).
////            this.getLedgerConnectionManager().notifyEventListeners(localSourceAddress, ledgerTransferPreparedEvent);
////            this.getLedgerConnectionManager().notifyEventListeners(
////                    localDestinationAddress, ledgerTransferPreparedEvent);
////        }
////    }
//
////    /**
////     * Completes the supplied {@link LedgerTransfer} using another connector.
////     *
////     * @param transferFunds An instance of {@link LedgerTransfer} to complete remotely.
////     */
////    private void sendRemotelyUsingUniversalMode(final LedgerTransfer transferFunds) {
////        Objects.requireNonNull(transferFunds);
////        throw new RuntimeException("Not yet implemented!");
////
//////        // In order to initiate a remote transferFunds, the LedgerTransferInputs needs to be transformed into
//////        // a LedgerTransfer that contains local account information.
//////
//////        // Find appropriate routeable Connector
//////        final ConnectorInfo connectorInfo = quotingService.findBestConnector(transferFunds)
//////                // TODO: Throw something better here
//////                .orElseThrow(() -> new InvalidQuoteRequestException("No Connector available")
//////                );
//////
//////        // PUT Money on-hold toLedgerId the source, for a Connector...
//////        final IlpAddress connectorIlpAddress = this.getLocalLedgerIlpAddressForConnector(connectorInfo.getConnectorId())
//////                .orElseThrow(() -> new AccountNotFoundException(
//////                        "ConnectorId: " + connectorInfo.getConnectorId().getId())
//////                );
//////
//////        // Compute the local source account for the transferFunds.  This is going to come toLedgerId the event.
//////        final IlpAddress localSourceAccountId = transferFunds.getLocalSourceAddress();
//////
//////        // The local destination account for the transferFunds is the designated Connector's account.  This is
//////        // only stored in Escrow.
//////        final IlpAddress localDestinationAccountId = connectorIlpAddress;
//////
//////        final EscrowInputs escrowInputs = EscrowInputs.builder()
//////                .interledgerPacketHeader(transferFunds.getInterledgerPacketHeader())
//////                .localSourceAddress(localSourceAccountId)
//////                .localDestinationAddress(localDestinationAccountId)
//////                .amount(transferFunds.getInterledgerPacketHeader().getDestinationAmount())
//////                .build();
//////
//////        // Execute Escrow!
//////        final Escrow escrow = this.escrowManager.initiateEscrow(escrowInputs);
//////
//////        // If this is an optimistic mode transferFunds, then fulfill the transferFunds immediately if the
//////        // destinationAddress is non-local (Only the final transferFunds of an optimistic-mode transferFunds is held in escrow).
//////        if (transferFunds.getInterledgerPacketHeader().isOptimisticModeHeader()) {
//////            if (transferFunds.getInterledgerPacketHeader().getDestinationAddress().getLedgerId().equals(
//////                    this.getLedgerInfo().getLedgerId()) != true) {
//////                this.fulfillCondition(transferFunds.getInterledgerPacketHeader().getIlpPaymentId());
//////            }
//////            // Otherwise, this is an optimistic-mode transferFunds, and the recipient needs to trigger a fulfillment by accepting the transferFunds.
//////            else {
//////                // Wait for the recipient to accept the optimistic payment.
//////                // TODO Notify the Wallet.
//////            }
//////        } else {
//////            // Notify any Listening Connectors that they have assets waiting!
//////            this.getLedgerConnectionManager().notifyEventListeners(
//////                    connectorInfo.getOptIlpAddress().get(),
//////                    new LedgerTransferPreparedEvent(
//////                            this.getLedgerInfo(),
//////                            transferFunds.getInterledgerPacketHeader(),
//////                            // TODO: Should the events use an ILPAddress instead?
//////                            escrow.getLocalSourceAddress().value(),
//////                            escrow.getLocalDestinationAddress().value(),
//////                            escrow.getDestinationAmount()
//////                    )
//////            );
//////        }
////    }
//
//    @Override
//    public void onEscrowTimedOut(final Escrow expiredEscrow) {
//        this.rejectTransfer(
//                expiredEscrow.getInterledgerPacketHeader().getIlpPaymentId(),
//                LedgerTransferRejectedReason.TIMEOUT
//        );
//    }
//
//
////    /**
////     * Helper method to transform an instance of {@link LedgerTransferInputs} into an instance of {@link LedgerTransfer}
////     * by looking up appropriate local account identifiers and populating them into the returned object.  For example,
////     * if a request comes in to transferFunds between ILP address 1 and ILP address 2, then the local accounts might be
////     * different in order to satisfy the connection.
////     *
////     * @param transferInputs
////     * @param connectorInfo  An instance of {@link ConnectorInfo} that contains information necessary to determine local
////     *                       account identifiers.
////     * @return
////     */
////    @VisibleForTesting
////    LedgerTransfer<String, NoteToSelf> toLedgerTransfer(
////            final LedgerTransferInputs transferInputs, final ConnectorInfo connectorInfo
////    ) {
////
////
////        final IlpAddress localSourceAddress = this.getLedgerAccountManager().getAccount()
////        final IlpAddress localDestinationAddress = this.getLedgerAccountManager().getAccount();
////
////        return DefaultLedgerTransfer.builder()
////                .interledgerPacketHeader(transferInputs.getInterledgerPacketHeader())
////                .localSourceAddress(localSourceAddress)
////                .localDestinationAddress(localDestinationAddress)
////                .amount(transferInputs.getDestinationAmount())
////                .optData(transferInputs.getOptData())
////                .optNoteToSelf(transferInputs.getOptNoteToSelf())
////                .build();
////    }
//
//////////////////////////////
//// Internal Implementations
//////////////////////////////
//
//    @RequiredArgsConstructor
//    public class InMemoryLedgerAccountManager implements LedgerAccountManager {
//
//        @NonNull
//        @Getter
//        private final LedgerInfo ledgerInfo;
//
//        // Local identifier to account mapping...Consider a Set here?
//        @NonNull
//        private final Map<IlpAddress, LedgerAccount> accounts;
//
////        // A collection of transfers for a given IlpPaymentId...
////        @NonNull
////        @Getter
////        private final Multimap<IlpPaymentId, LedgerAccountTransfer> transfers;
//
//        private InMemoryLedgerAccountManager(final LedgerInfo ledgerInfo) {
//            this.ledgerInfo = Objects.requireNonNull(ledgerInfo);
//            this.accounts = new HashMap<>();
//            //this.transfers = ArrayListMultimap.create();
//        }
//
//
//        /**
//         * A helper method to initialize an account with a given balance, for testing purposes.
//         *
//         * @param ilpAddress
//         * @param initialAmount
//         * @return
//         * @deprecated This method is deprecated because it only exists for testing purposes.  At the Ledger interface
//         * layer, account creation should not be happening as part of the ILP process -- it should instead be happening
//         * out of band.
//         */
//        @Deprecated
//        public SimpleLedgerAccount createAccount(
//                final IlpAddress ilpAddress, final MonetaryAmount initialAmount
//        ) {
//            final SimpleLedgerAccount newAccount = new SimpleLedgerAccount(
//                    LedgerAccountId.of(UUID.randomUUID().toString()), ilpAddress,
//                    initialAmount
//            );
//            this.getAccount(ilpAddress).ifPresent(ledgerAccount -> {
//                throw new RuntimeException(String.format("Account %s already exists!", ilpAddress));
//            });
//            this.accounts.put(ilpAddress, newAccount);
//            return newAccount;
//        }
//
//        @Override
//        public Optional<LedgerAccount> getAccount(final IlpAddress ilpAddress)
//                throws InvalidAccountException {
//            Objects.requireNonNull(ilpAddress);
//
//            final LedgerId ledgerId = this.getLedgerInfo().getLedgerId();
//            Preconditions.checkArgument(
//                    ilpAddress.getLedgerId().equals(ledgerId), String.format(
//                            "Can't retrieve account for foreign ILP Address (%s) on Ledger (%s)!",
//                            ilpAddress,
//                            ledgerId
//                    )
//            );
//
//            return Optional.ofNullable(this.accounts.get(ilpAddress));
//        }
//
//        @Override
//        public Collection<LedgerAccount> getAccountIds(int page, int pageSize) {
//            // No paging necessary since this is just an in-memory implementation.
//            return this.accounts.values();
//        }
//
//        /**
//         * This implementation requires that the account to be debited exist in order for this operation to succeed.
//         *
//         * @param ilpAddress
//         * @param amount
//         * @return
//         */
////        @Override
////        public LedgerAccount creditAccount(final IlpAddress ilpAddress, final MonetaryAmount amount) {
////            Objects.requireNonNull(ilpAddress);
////            Objects.requireNonNull(amount);
////            Preconditions.checkArgument(amount.isPositiveOrZero(), "Transfers must be $0 or greater!");
////            Preconditions.checkArgument(
////                    amount.getCurrency().getCurrencyCode().equals(this.getLedgerInfo().getCurrencyCode()),
////                    "Transfers must specify the same currency code as this Ledger!"
////            );
////
////            final LedgerAccount creditedLedgerAccount = this.getAccount(ilpAddress)
////                    .map(ledgerAccount -> new SimpleLedgerAccount(
////                            ledgerAccount.getLedgerAccountIlpAddress(), ledgerAccount.getIlpIdentifier(),
////                            ledgerAccount.getBalance().add(amount)
////                    )).orElseThrow(() -> new RuntimeException("No account exists for " + ilpAddress));
////
////            return this.accounts.put(ilpAddress, creditedLedgerAccount);
////        }
//
//
//        /**
//         * This implementation requires that the account to be debited exist in order for this operation to succeed.
//         *
//         * @param ilpAddress
//         * @param amount
//         * @return
//         */
////        @Override
////        public LedgerAccount debitAccountId(final IlpAddress ilpAddress, final MonetaryAmount amount) {
////            Objects.requireNonNull(ilpAddress);
////            Objects.requireNonNull(amount);
////            Preconditions.checkArgument(
////                    amount.getCurrency().getCurrencyCode().equals(this.getLedgerInfo().getCurrencyCode()),
////                    "Transfers must specify the same currency code as this Ledger!"
////            );
////
////            final LedgerAccount debitedLedgerAccount = this.getAccount(ilpAddress)
////                    .map(ledgerAccount -> new SimpleLedgerAccount(
////                            ledgerAccount.getLedgerAccountIlpAddress(), ledgerAccount.getIlpIdentifier(),
////                            ledgerAccount.getBalance().subtract(amount)
////                    )).orElseThrow(() -> new RuntimeException("No account exists for " + ilpAddress));
////
////            // Disallow the account toLedgerId going negative...
////            Preconditions.checkArgument(debitedLedgerAccount.getBalance().isPositiveOrZero());
////
////            return this.accounts.put(ilpAddress, debitedLedgerAccount);
////        }
//
//        /**
//         * WARNING: This operation is non-atomic.  If either fails, the initiateEscrow will be corrupted!
//         */
//        @Override
//        public void transferFunds(
//                final IlpAddress localSourceAddress,
//                final IlpAddress localDestinationAddress,
//                final MonetaryAmount amount
//        ) {
//            Objects.requireNonNull(localSourceAddress);
//            Objects.requireNonNull(localDestinationAddress);
//            Objects.requireNonNull(amount);
//
//            Preconditions.checkArgument(
//                    amount.getCurrency().getCurrencyCode().equals(this.getLedgerInfo().getCurrencyCode()),
//                    "Transfers must specify the same currency code as this Ledger!"
//            );
//
//            ///////////////////
//            // DEBIT
//            ///////////////////
//            {
//                final LedgerAccount debitedLedgerAccount = this.getAccount(localSourceAddress)
//                        .map(ledgerAccount -> new SimpleLedgerAccount(
//                                ledgerAccount.value(), ledgerAccount.getIlpIdentifier(),
//                                ledgerAccount.getBalance().subtract(amount)
//                        )).orElseThrow(() -> new RuntimeException(
//                                "No account exists for transferFunds local destinationAddress: " + localSourceAddress));
//                // Disallow the account toLedgerId going negative...
//                Preconditions.checkArgument(debitedLedgerAccount.getBalance().isPositiveOrZero());
//                this.accounts.put(localSourceAddress, debitedLedgerAccount);
//            }
//
//            ///////////////////
//            // CREDIT
//            ///////////////////
//            {
//                final LedgerAccount creditedLedgerAccount = this.getAccount(localDestinationAddress)
//                        .map(ledgerAccount -> new SimpleLedgerAccount(
//                                ledgerAccount.value(), ledgerAccount.getIlpIdentifier(),
//                                ledgerAccount.getBalance().add(amount)
//                        )).orElseThrow(() -> new RuntimeException(
//                                "No account exists for transferFunds local destinationAddress: " + localDestinationAddress));
//                // Disallow the account toLedgerId going negative...
//                Preconditions.checkArgument(creditedLedgerAccount.getBalance().isPositiveOrZero());
//                this.accounts.put(localDestinationAddress, creditedLedgerAccount);
//            }
//
//
////            // Track the transferFunds for later...
////            final LedgerAccountTransfer ledgerAccountTransfer = LedgerAccountTransfer.builder()
////                    .ilpTransactionId(ilpTransactionId)
////                    .transferDateTime(ZonedDateTime.now(DateTimeZone.UTC))
////                    .localSourceAddress(localSourceAddress)
////                    .localDestinationAddress(localDestinationAddress)
////                    .amount(amount)
////                    .build();
////
////            this.transfers.put(ilpTransactionId, ledgerAccountTransfer);
//        }
//
////        public Optional<LedgerAccountTransfer> getLatestLedgerAccountTransfer(final IlpPaymentId ilpTransactionId) {
////            Objects.requireNonNull(ilpTransactionId);
////            final Collection<LedgerAccountTransfer> ledgerAccountTransfers = Optional.ofNullable(
////                    this.getTransfers().get(ilpTransactionId)).orElseThrow(() -> {
////                throw new RuntimeException(
////                        String.format("No Transfer existed for IlpTransacitonId %s", ilpTransactionId));
////            });
////
////            // Find the latest transferFunds...
////            return ledgerAccountTransfers.stream().reduce(
////                    (LedgerAccountTransfer a, LedgerAccountTransfer b) -> {
////                        if (a.compareTo(b) > 0) {
////                            return a;
////                        } else {
////                            return b;
////                        }
////                    });
////        }
//
//
////        /**
////         * WARNING: This operation is non-atomic.  If either fails, the initiateEscrow will be corrupted!
////         */
////        @Override
////        public void transferLocal(
////                final IlpAddress localSourceAddress,
////                final IlpAddress localDestinationAddress,
////                final MonetaryAmount amount
////        ) {
////            Objects.requireNonNull(localSourceAddress);
////            Objects.requireNonNull(localDestinationAddress);
////            Objects.requireNonNull(amount);
////            Preconditions.checkArgument(
////                    amount.getCurrency().getCurrencyCode().equals(this.getLedgerInfo().getCurrencyCode()),
////                    "Transfers must specify the same currency code as this Ledger!"
////            );
////
////            ///////////////////
////            // DEBIT
////            ///////////////////
////            {
////                final LedgerAccount debitedLedgerAccount = this.getAccount(localSourceAddress)
////                        .map(ledgerAccount -> new SimpleLedgerAccount(
////                                ledgerAccount.getLedgerAccountIlpAddress(), ledgerAccount.getIlpIdentifier(),
////                                ledgerAccount.getBalance().subtract(amount)
////                        )).orElseThrow(() -> new RuntimeException(
////                                "No account exists for transferFunds local destinationAddress: " + localSourceAddress));
////                // Disallow the account toLedgerId going negative...
////                Preconditions.checkArgument(debitedLedgerAccount.getBalance().isPositiveOrZero());
////                this.accounts.put(localSourceAddress, debitedLedgerAccount);
////            }
////
////            ///////////////////
////            // CREDIT
////            ///////////////////
////            {
////                final LedgerAccount creditedLedgerAccount = this.getAccount(localDestinationAddress)
////                        .map(ledgerAccount -> new SimpleLedgerAccount(
////                                ledgerAccount.getLedgerAccountIlpAddress(), ledgerAccount.getIlpIdentifier(),
////                                ledgerAccount.getBalance().add(amount)
////                        )).orElseThrow(() -> new RuntimeException(
////                                "No account exists for transferFunds local destinationAddress: " + localDestinationAddress));
////                // Disallow the account toLedgerId going negative...
////                Preconditions.checkArgument(creditedLedgerAccount.getBalance().isPositiveOrZero());
////                this.accounts.put(localDestinationAddress, creditedLedgerAccount);
////            }
////        }
//    }
//
//    /**
//     * An internal implementation of {@link LedgerConnectionManager} that handles all connections for listening
//     * connectors.
//     */
//    @RequiredArgsConstructor
//    public class InMemoryLedgerConnectionManager implements LedgerConnectionManager {
//
//        // Information about the Ledger that this connection manager operates on behalf of.
//        @NonNull
//        @Getter
//        private final LedgerInfo ledgerInfo;
//
//        // Each time a Connector or Wallet connects to this Ledger, a LedgerEventListener will be added to this list
//        // with a unique connection id represented by the LedgerAccountId.
//
//        // NOTE: For real applications, consider using something akin to Guava's SubscriberRegistry found in EventBus.java.
//        //@NonNull
//        //private final Map<ConnectorId, LedgerEventListener> ledgerEventListeners;
//
//        // For a given IlpAddress, there is only one LedgerEventListener, which can contain multiple event handlers.
//        @NonNull
//        private final Map<IlpAddress, LedgerEventListener> ledgerEventListeners;
//
//        /**
//         * Helper constructor.
//         *
//         * @param ledgerInfo
//         */
//        public InMemoryLedgerConnectionManager(final LedgerInfo ledgerInfo) {
//            this.ledgerInfo = Objects.requireNonNull(ledgerInfo);
//            this.ledgerEventListeners = new HashMap<>();
//        }
//
//        @Override
//        public void connect(final ConnectionInfo connectionInfo) {
//            // TODO: Validate authentication!
//            // TODO: Validate inputs!
//
//            // For a given LedgerEventListener, this implementation should have a mechanism to callback to the Connector
//            // that registered the listener.  This could be something like a Webhook, or it could be a direct in-process
//            // call.
//
//            // For each ILP connection to this Ledger, a LedgerEventListener is added to the list of listeners.  This
//            // listener contains one or more handlers that should connect back to the client (i.e., Connector or Wallet)
//            // that just connected.  This layer is necessary in order to restrict callbacks/notifications to only the
//            // account that should receive the notifications.
//
//            // final ConnectorInfo connectorInfo = ConnectorInfo.builder()
//            //       .il(connectionInfo.getConnectorId())
//            // TODO: Determine how the Ledger gets the account of the Connector so that when money is sent by a
//            // connector to a local destination, the Ledger know who to call-back.
//            //     .optLedgerAccountId(Optional.of(LedgerAccountId.of(connectionInfo.getClientId()))).build();
//            final LedgerEventListener ledgerEventListener = new InMemoryLedgerEventListener(
//                    this.getLedgerInfo(), connectionInfo.getLedgerAccountIlpAddress());
//            this.ledgerEventListeners.put(connectionInfo.getLedgerAccountIlpAddress(), ledgerEventListener);
//        }
//
//        @Override
//        public void disconnect(final IlpAddress ilpAddress) {
//            // TODO: Validate authentication!
//            // TODO: Validate inputs!
//            this.ledgerEventListeners.remove(ilpAddress);
//        }
//
//        @Override
//        public void notifyEventListeners(
//                final IlpAddress targetedIlpAddress, LedgerEvent ledgerEvent
//        ) {
//            Objects.requireNonNull(targetedIlpAddress);
//            Objects.requireNonNull(ledgerEvent);
//
//            this.ledgerEventListeners.values().stream()
//                    // Only listeners with the proper targetedIlpAddress target
//                    .filter(listener -> listener.getListeningIlpAddress().equals(targetedIlpAddress))
//                    .forEach(listener -> listener.notifyEventHandlers(ledgerEvent));
//        }
//
//        @Override
//        public void registerEventHandler(
//                final IlpAddress ilpAddress, final LedgerEventHandler ledgerEventHandler
//        ) {
//            // If the listener is present, just add the handler.  Otherwise, throw an exception.
//
//            final Optional<LedgerEventListener> optLedgerEventListener = Optional.ofNullable(
//                    this.ledgerEventListeners.get(ilpAddress));
//
//            if (optLedgerEventListener.isPresent()) {
//                optLedgerEventListener.get().registerEventHandler(ledgerEventHandler);
//            } else {
//                throw new RuntimeException(String.format(
//                        "No LedgerId registered for IlpAddress '%s'.  Call the connect() method first before trying to register!",
//                        ilpAddress
//                ));
//            }
//        }
//
//    }
//
//    /**
//     * An implementation of {@link LedgerEventListener} that allows for a collection of {@link LedgerEventHandler}
//     * to be associated to a single Ledger/Connector pair.  This implementation will be used by an {@link
//     * InMemoryLedger} to emit all types of {@link LedgerEvent} to a single Connector.  In other words, every
//     * connector that connects to this ledger will have one instances of this class constructed for it, with N
//     * number of handlers found inside.
//     * <p>
//     * NOTE: Per the docs in {@link LedgerEventListener}, there is a one-to-one relationship between an instance of
//     * this listener, a connector and a ledger (in this case, the single ledger in the in-memory ledger).
//     * Registering instances of {@link LedgerEventHandler} that apply to more than one ledger or connector could
//     * cause security issues because this implementation notifies all handlers on the assumption that it contains
//     * handlers for only a single connector/ledger pair.
//     */
//    @Getter
//    @RequiredArgsConstructor
//    public static class InMemoryLedgerEventListener implements LedgerEventListener {
//
//        // Info about the ledger that this listener is listening to.
//        @NonNull
//        private final LedgerInfo ledgerInfo;
//
//        // The connector that this listener is listening on behalf of.  This is necessary for the consumers of this
//        // listener to properly route events
//        //@NonNull
//        //private final ConnectorInfo connectorInfo;
//        @NonNull
//        private final IlpAddress listeningIlpAddress;
//
//        // This listener might have a single handler (for all events) or it might have a single handler for each event.
//        // However, this always only connects a single ledger/connector pair.
//        @NonNull
//        private final Set<LedgerEventHandler> ledgerEventHandlers;
//
//        /**
//         * Helper Constructor.  Initializes the {@link List} of {@link LedgerEventHandler}'s to an empty list.
//         *
//         * @param ledgerInfo
//         * @param listeningIlpAddress
//         */
//        public InMemoryLedgerEventListener(
//                final LedgerInfo ledgerInfo, final IlpAddress listeningIlpAddress
//        ) {
//            this.ledgerInfo = Objects.requireNonNull(ledgerInfo);
//            this.listeningIlpAddress = Objects.requireNonNull(listeningIlpAddress);
//            this.ledgerEventHandlers = new HashSet<>();
//        }
//
//        // TODO: consider returning a boolean to align with java.util.Collection - indicates if the handler already existed?
//        @Override
//        public void registerEventHandler(LedgerEventHandler<?> handler) {
//            Preconditions.checkNotNull(handler);
//
//            // Add the handler to the list...
//            this.ledgerEventHandlers.add(handler);
//
//            // Any handlers in this class should be notified, since they're all limited to a single connector.
//            this.notifyEventHandlers(new LedgerConnectedEvent(this.getLedgerInfo()));
//        }
//
//        @Override
//        public void unRegisterEventHandlers() {
//            // TODO: Is this necessary?  There's a contradiction between a Connector being disconnected toLedgerId a ledger and
//            // a ledger trying to send the disconnected Connector an event that says the disconnect worked.
//            //this.notifyEventHandlers(new LedgerDisonnectedEvent(this.getLedgerInfo(), this.getConnectorInfo()));
//
//            // Remove all envet handlers...
//            this.ledgerEventHandlers.clear();
//        }
//
//        @Override
//        public void unRegisterEventHandler(LedgerEventHandler<?> handler) {
//            // Remove the handler toLedgerId the handler list...
//            this.ledgerEventHandlers.remove(handler);
//        }
//
//        @Override
//        public void notifyEventHandlers(final LedgerEvent ledgerEvent) {
//            // Notify all handlers.  If this notify handler is getting called, it means there's an event for this ledger/connector combination.
//            for (final LedgerEventHandler handler : this.ledgerEventHandlers) {
//                handler.onLedgerEvent(ledgerEvent);
//            }
//        }
//    }
}