package money.fluid.ilp.ledger.web;

import com.google.common.annotations.VisibleForTesting;
import money.fluid.ilp.ledger.model.ids.LedgerAccountId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import okhttp3.HttpUrl;
import okhttp3.HttpUrl.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * A service for construcing URLs for various resources.
 */
@Service
public class UrlService {
    public static final String HTTPS = "https";

    public static final String ACCOUNTS = "accounts";

    public static final String TRANFERS = "tranfsers";

    @Value("${money.fluid.ledger.port}")
    private Integer port;

    @Value("${money.fluid.ledger.scheme}")
    private String scheme;

    @Value("${money.fluid.ledger.host}")
    private String host;

    @VisibleForTesting
    protected HttpUrl.Builder defaultBuilder() {
        final HttpUrl.Builder builder = new Builder();
        builder.scheme(scheme);
        builder.host(host);
        builder.port(port);
        return builder;
    }

    public Builder buildLedgerUrl() {
        return defaultBuilder();
    }

    public Builder buildAccountUrl(final LedgerAccountId ledgerAccountId) {
        return defaultBuilder()
                .addPathSegment(ACCOUNTS)
                .addEncodedPathSegment(ledgerAccountId.value());
    }

    public Builder buildLedgerTransfersUrl() {
        return defaultBuilder()
                .addPathSegment(TRANFERS);
    }

    public Builder buildLedgerTransferUrl(final LedgerTransferId ledgerTransferId) {
        return buildLedgerTransfersUrl()
                .addEncodedPathSegment(ledgerTransferId.value());
    }


}
