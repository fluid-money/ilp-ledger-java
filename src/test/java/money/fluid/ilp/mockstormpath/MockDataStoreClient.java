package money.fluid.ilp.mockstormpath;

import com.google.common.collect.ImmutableMap;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.ds.DataStore;
import com.stormpath.sdk.impl.api.ApiKeyResolver;
import com.stormpath.sdk.impl.api.DefaultApiKeyResolver;
import com.stormpath.sdk.impl.application.DefaultApplication;
import com.stormpath.sdk.impl.authc.credentials.ApiKeyCredentials;
import com.stormpath.sdk.impl.authc.credentials.ClientCredentials;
import com.stormpath.sdk.impl.client.DefaultClient;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.http.RequestExecutor;
import com.stormpath.sdk.impl.util.BaseUrlResolver;
import com.stormpath.sdk.impl.util.DefaultBaseUrlResolver;
import com.stormpath.sdk.resource.Resource;
import org.mockito.Mockito;

import java.util.Map;


/**
 * In the Stormpath API the Client is the first thing to be created - everything else is created via that Client, and
 * the Client's DataStore is the thing that handles the lower level interaction with the remote Stormpath service.
 * <p>
 * This class provides a Client which has a DataStore that is mocked out so that it doesn't make remote calls.
 * <p>
 * Use this class in your tests to replace the bean that's otherwise provided by StormpathAutoConfiguration.stormpathClient().
 * <p>
 * Subclass and override createDataStore(...) and/or createApplication(...) to provide further mocked behavior.
 *
 * @see "https://github.com/george-hawkins-aa/mocked-data-store-client"
 */
public class MockDataStoreClient extends DefaultClient {
    private final static String STORMPATH_BASE_URL = "https://api.stormpath.com/v1";

    public MockDataStoreClient(final ApiKey apiKey, final CacheManager cacheManager) {
        super(
                new ApiKeyCredentials(apiKey),
                new DefaultApiKeyResolver(apiKey),
                new DefaultBaseUrlResolver(STORMPATH_BASE_URL),
                null,
                cacheManager,
                null,
                null,
                1
        );
    }

    @Override
    protected DataStore createDataStore(
            RequestExecutor requestExecutor, BaseUrlResolver baseUrlResolver, ClientCredentials clientCredentials,
            ApiKeyResolver apiKeyResolver, CacheManager cacheManager
    ) {
        final InternalDataStore store = Mockito.mock(InternalDataStore.class, Mockito.RETURNS_SMART_NULLS);

        Mockito.when(store.getApiKey()).thenReturn(apiKeyResolver.getApiKey());
        Mockito.when(store.getCacheManager()).thenReturn(cacheManager);

        Application application = createApplication(store);

        Mockito.when((Resource) store.getResource(Mockito.anyString(), Mockito.eq(Application.class))).thenReturn(
                application);

        return store;
    }

    protected Application createApplication(InternalDataStore store) {
        return new DefaultApplication(store);
    }


}