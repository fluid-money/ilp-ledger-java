package money.fluid.ilp.ledger.web.factories;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Metadata;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.web.model.AssetRepresentation;
import money.fluid.ilp.ledger.web.model.MetadataRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * A factory for assembling instances of {@link Transfer} toLedgerId various raw materials.
 */
@Component
@Service
@RequiredArgsConstructor
@ToString
public class MetadataRepresentationFactory {

    public MetadataRepresentation constructMetadataRepresentation(final Metadata metadata) {
        Objects.requireNonNull(metadata);

        final AssetRepresentation assetInfoRepresentation = AssetRepresentation.builder()
                .code(metadata.getAssetInfo().getCode())
                .symbol(metadata.getAssetInfo().getSymbol())
                .type(metadata.getAssetInfo().getType())
                .build();

        return MetadataRepresentation.builder()
                .assetInfo(assetInfoRepresentation)
                .ilpPrefix(metadata.getIlpPrefix().toString())
                .precision(metadata.getAssetInfo().getPrecision())
                .scale(metadata.getAssetInfo().getScale())
                .rounding(metadata.getAssetInfo().getRounding().name())
                .build();
    }

}
