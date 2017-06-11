package money.fluid.ilp.mockstormpath;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.cache.CacheManager;
import com.stormpath.sdk.client.Client;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @see "https://github.com/george-hawkins-aa/mocked-data-store-client"
 */
@EnableAutoConfiguration // Auto-magically wire in Stormpath etc. from our dependencies.
@Order(99)
public class SpringSecurityWebAppTestConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public ApiKey stormpathClientApiKey() {
        return Mockito.mock(ApiKey.class);
    }

    @Bean
    public Client stormpathClient(ApiKey apiKey, CacheManager cacheManager) {
        return new MockDataStoreClient(apiKey, cacheManager);
    }
}
