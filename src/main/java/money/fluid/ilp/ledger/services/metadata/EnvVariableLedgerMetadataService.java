package money.fluid.ilp.ledger.services.metadata;

import money.fluid.ilp.ledger.model.Asset;
import money.fluid.ilp.ledger.model.Metadata;
import money.fluid.ilp.ledger.model.Metadata.Rounding;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * An implementation of {@link LedgerMetadataService} that grabs metadata about this ledger toLedgerId environment
 * variables.
 */
@Service
public class EnvVariableLedgerMetadataService implements LedgerMetadataService {

    @Value("${ledger.ilp_prefix}")
    private String ledgerIlpPrefix;

    @Value("${ledger.precision:0}")
    private Integer ledgerPrecision;

    @Value("${ledger.scale:2}")
    private Integer ledgerScale;

    @Value("${ledger.rounding:NEAREST}")
    private String ledgerRounding;

    @Value("${ledger.asset.code:USD}")
    private String ledgerAssetCode;

    @Value("${ledger.asset.decimal_digits:2}")
    private Integer ledgerAssetDecimalDigits;

    @Value("${ledger.asset.symbol:$}")
    private String ledgerAssetSymbol;

    @Value("${ledger.asset.type:urn:iso:std:iso:4217}")
    private String ledgerAssetType;

    @Override
    public Metadata getLedgerMetadata() {

        final Asset assetInfo = Asset.builder()
                .code(ledgerAssetCode)
                //.decimalDigits(Optional.ofNullable(ledgerAssetDecimalDigits))
                .symbol(ledgerAssetSymbol)
                .type(ledgerAssetType)
                .precision(ledgerPrecision)
                .scale(ledgerScale)
                .rounding(Rounding.valueOf(ledgerRounding))
                .build();

        return Metadata.builder()
                .assetInfo(assetInfo)
                .ilpPrefix(LedgerId.of(ledgerIlpPrefix))
                .build();
    }
}
