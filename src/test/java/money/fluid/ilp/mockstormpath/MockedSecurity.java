package money.fluid.ilp.mockstormpath;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.impl.directory.DefaultCustomData;
import com.stormpath.sdk.impl.ds.InternalDataStore;
import com.stormpath.sdk.impl.group.DefaultGroupList;
import com.stormpath.sdk.servlet.account.DefaultAccountResolver;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * If using MockMvc and MockHttpServletRequestBuilder then use:
 * <p>
 * mockedSecurity.addAuthentication(builder)
 *
 * @see "https://github.com/george-hawkins-aa/mocked-data-store-client"
 */
public class MockedSecurity {
    public void addAuthentication(MockHttpServletRequestBuilder builder) {
        // This installs the Account that we can retrieve in our own code with AccountResolver.INSTANCE.getAccount(request).
        // But more importantly it's the attribute read by SpringSecurityResolvedAccountFilter.filter(...) that will setup
        // the authentication needed to enter @Secured annotated methods etc.
        builder.requestAttr(DefaultAccountResolver.REQUEST_ATTR_NAME, createMockAccount());
    }

    // Create an account with  the minimum needed to satisfy the Stormpath resolved account logic.
    protected Account createMockAccount() {
        InternalDataStore dataStore = Mockito.mock(InternalDataStore.class);
        final DefaultCustomData customData = new DefaultCustomData(dataStore);

        Account account = Mockito.mock(Account.class);

        // Email, groups and custom data need to return values in order to reach and get past StormpathAuthenticationProvider.authenticate(...).
        Mockito.when(account.getHref()).thenReturn("https://api.stormpath.com/v1/accounts/invalid");
        Mockito.when(account.getEmail()).thenReturn("john.smith@example.com");
        Mockito.when(account.getGroups()).thenReturn(new DefaultGroupList(dataStore));
        Mockito.when(account.getCustomData()).thenReturn(customData);

        return account;
    }
}