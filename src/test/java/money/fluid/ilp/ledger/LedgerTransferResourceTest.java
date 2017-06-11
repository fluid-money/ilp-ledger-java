package money.fluid.ilp.ledger;

import money.fluid.ilp.ledger.config.Application;
import money.fluid.ilp.mockstormpath.MockedSecurity;
import money.fluid.ilp.mockstormpath.SpringSecurityWebAppTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Smoke tests for the /transfers resource endpoint...
 *
 * @see "https://spring.io/blog/2016/04/15/testing-improvements-in-spring-boot-1-4"
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(
        classes = {
                Application.class,
                SpringSecurityWebAppTestConfig.class
        },
        webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test-application.properties")
public class LedgerTransferResourceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private final MockedSecurity security = new MockedSecurity();

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**
     * Expect the correct ledger metadata to be returned by the ledger, if authenticated properly.
     */
    @Test
    public void getRootTest() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/");

        // First try without authorization and confirm it fails...
        mockMvc.perform(builder).andExpect(status().isUnauthorized());

        // Now mark as authorized and try again...
        security.addAuthentication(builder);

        String expectedJson = "{\"asset_info\":{\"type\":\"urn:iso:std:iso:4217\",\"code\":\"USD\",\"symbol\":\"$\"},\"ilp_prefix\":\"us.usd.money.fluid.ledger\",\"precision\":8,\"scale\":2,\"rounding\":\"NEAREST\"}";
        ResultMatcher expectedContent = content().json(expectedJson);

        final ResultActions result = mockMvc.perform(builder);

        result.andExpect(status().isOk()).andExpect(expectedContent);
    }

    /**
     * Expect the correct ledger metadata to be returned by the ledger, if authenticated properly.
     */
    @Test
    public void getAccountForBob() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/accounts/bob");

        // First try without authorization and confirm it fails...
        mockMvc.perform(builder).andExpect(status().isUnauthorized());

        // Now mark as authorized and try again...
        security.addAuthentication(builder);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value("http://localhost:8080/accounts/bob"))
                .andExpect(jsonPath("$.name").value("bob"))
                .andExpect(jsonPath("$.minimum_allowed_balance").value("0"))
                .andExpect(jsonPath("$.balance").value("100"))
                .andExpect(jsonPath("$.ledger").value("http://localhost:8080/"));
    }

    /**
     * Expect the correct ledger metadata to be returned by the ledger, if authenticated properly.
     */
    @Test
    public void getAccountForAlice() throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/accounts/alice");

        // First try without authorization and confirm it fails...
        mockMvc.perform(builder).andExpect(status().isUnauthorized());

        // Now mark as authorized and try again...
        security.addAuthentication(builder);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value("http://localhost:8080/accounts/alice"))
                .andExpect(jsonPath("$.name").value("alice"))
                .andExpect(jsonPath("$.minimum_allowed_balance").value("0"))
                .andExpect(jsonPath("$.balance").value("100"))
                .andExpect(jsonPath("$.ledger").value("http://localhost:8080/"));
    }
}