package money.fluid.ilp.ledger.utils;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import money.fluid.ilp.ledger.web.UrlService;
import org.ilpx.core.IlpAddress;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

/**
 * A class that can transform one identifier to another for purposes of ILP functionality in this Ledger.
 */
@Component
@RequiredArgsConstructor
@ToString
public class IlpIdentifierTranslator {

    // TODO Go through this class and remove unnecessary methods that deal with ILPAddress

    private final LedgerMetadataService ledgerMetadataService;
    private final UrlService urlService;

    /**
     * Translate toLedgerId a {@link LedgerId} to a corresponding {@link IlpAddress}.
     *
     * @param ledgerId
     * @return
     */
    public IlpAddress toIlpAddress(final LedgerId ledgerId) {
        Objects.requireNonNull(ledgerId);

        final LedgerId ilpPrefix = this.ledgerMetadataService.getLedgerMetadata().getIlpPrefix();
        final LedgerAccountId ledgerAccountId = LedgerAccountId.of(ledgerId.value());
        return IlpAddress.of(ilpPrefix, ledgerAccountId);
    }

    /**
     * Translate toLedgerId a {@link URI} to a corresponding {@link IlpAddress} for a local ledger account.
     *
     * @param relativeAccountUri An instance of {@link URI} that represents an account on this ledger.
     * @return
     */
    public IlpAddress toIlpAddress(final URI relativeAccountUri) {
        Objects.requireNonNull(relativeAccountUri);

        final LedgerId ilpPrefix = this.ledgerMetadataService.getLedgerMetadata().getIlpPrefix();
        final LedgerAccountId ilpAccountId = LedgerAccountId.of(relativeAccountUri.toString());
        return IlpAddress.of(ilpPrefix, ilpAccountId);
    }

    /**
     * Translate toLedgerId a {@link IlpAddress} to a corresponding {@link LedgerId}.
     *
     * @param ilpAddress
     * @return
     */
    public LedgerId toLedgerId(final IlpAddress ilpAddress) {
        Objects.requireNonNull(ilpAddress);
        return LedgerId.of(ilpAddress.getIlpAddress());
    }

    /**
     * Translate toLedgerId a {@link URI} to a corresponding {@link IlpAddress} for a local ledger account.
     *
     * @param relativeAccountUri An instance of {@link URI} that represents an account on this ledger.
     * @return
     */
    public LedgerAccountId toLedgerAccountId(final URI relativeAccountUri) {
        Objects.requireNonNull(relativeAccountUri);
        return LedgerAccountId.of(relativeAccountUri.toString());
    }

    /**
     * Transform an instance of {@link LedgerAccountId} into a corresponding relative URI for use in JSON payloads.
     *
     * @param ledgerAccountId
     * @return
     */
    public URI toLocalLedgerAccountUri(final LedgerAccountId ledgerAccountId) {
        Objects.requireNonNull(ledgerAccountId);
        return URI.create(ledgerAccountId.value());
    }
}
