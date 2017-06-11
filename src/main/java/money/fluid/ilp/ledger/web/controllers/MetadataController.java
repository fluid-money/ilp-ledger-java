package money.fluid.ilp.ledger.web.controllers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Metadata;
import money.fluid.ilp.ledger.services.LedgerMetadataService;
import money.fluid.ilp.ledger.web.factories.MetadataRepresentationFactory;
import money.fluid.ilp.ledger.web.model.MetadataRepresentation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * A REST endpoint that allows external clients to retrieve metadata about this ledger.  This resource MUST be
 * accessible to all clients authorized as account owners or administrators.   This method MAY be accessible to
 * unauthorized users.
 */
@RestController
@RequiredArgsConstructor
@ToString
public class MetadataController {

    @NonNull
    private final MetadataRepresentationFactory medadataRepresentationFactory;

    @NonNull
    private final LedgerMetadataService ledgerMetadataService;

    /**
     * The main controller for providing Quotes to external callers.
     *
     * @return An instance of {@link MetadataRepresentation}
     */
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MetadataRepresentation getLedgerMetaData() {
        final Metadata metadata = this.ledgerMetadataService.getLedgerMetadata();
        return medadataRepresentationFactory.constructMetadataRepresentation(metadata);
    }


}
