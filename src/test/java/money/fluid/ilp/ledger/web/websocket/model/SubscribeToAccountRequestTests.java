package money.fluid.ilp.ledger.web.websocket.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import money.fluid.ilp.ledger.utils.LedgerObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for {@link SubscribeToAccountRequest}.
 */
public class SubscribeToAccountRequestTests {

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.objectMapper = new LedgerObjectMapper(2, 10, "NEAREST");
    }

    private final String SUBSCRIBE_ACCOUNT_JSON_NO_EVENT_TYPE = " {" +
            "  \"id\": \"1\"," +
            "  \"jsonrpc\": \"2.0\"," +
            "  \"method\": \"subscribe_account\"," +
            "    \"params\": {" +
            "    \"accounts\": [\"https://ledger.example/accounts/alice\"]" +
            "   }" +
            "}";

    /**
     * Not specifying the event_type should subscribe the user to everything.
     */
    @Test
    public void JsonTest_NoEventTypeSpecified() throws Exception {
        final SubscribeToAccountRequest subscribeToAccountRequest = this.objectMapper.readValue(
                SUBSCRIBE_ACCOUNT_JSON_NO_EVENT_TYPE, SubscribeToAccountRequest.class);

        assertThat(subscribeToAccountRequest.getId(), is("1"));
        assertThat(subscribeToAccountRequest.getJsonrpc(), is("2.0"));
        assertThat(subscribeToAccountRequest.getMethod(), is("subscribe_account"));

        assertThat(subscribeToAccountRequest.getParams().getEventType().isPresent(), is(false));
        //assertThat(subscribeToAccountRequest.getParams().getEventType().get(), is(LedgerResourceEvent.Event.ALL));

        final String serializedJson = this.objectMapper.writeValueAsString(subscribeToAccountRequest);

        assertThat(
                serializedJson,
                is("{\"id\":\"1\",\"jsonrpc\":\"2.0\",\"method\":\"subscribe_account\",\"params\":{\"event\":null,\"accounts\":[\"https://ledger.example/accounts/alice\"]}}")
        );
    }

    private final String SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_CREATE = " {" +
            "  \"id\": \"1\"," +
            "  \"jsonrpc\": \"2.0\"," +
            "  \"method\": \"subscribe_account\"," +
            "  \"params\": {" +
            "    \"event\": \"transferFunds.create\"," +
            "    \"accounts\": [\"https://ledger.example/accounts/alice\"]" +
            "   }" +
            "}";

    /**
     * Not specifying the event_type should subscribe the user to everything.
     */
    @Test
    public void JsonTest_TransferDotCreate() throws Exception {
        final SubscribeToAccountRequest subscribeToAccountRequest = this.objectMapper.readValue(
                SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_CREATE, SubscribeToAccountRequest.class);

        assertThat(subscribeToAccountRequest.getId(), is("1"));
        assertThat(subscribeToAccountRequest.getJsonrpc(), is("2.0"));
        assertThat(subscribeToAccountRequest.getMethod(), is("subscribe_account"));

        assertThat(subscribeToAccountRequest.getParams().getEventType().isPresent(), is(true));
        assertThat(subscribeToAccountRequest.getParams().getEventType().get(), is("transferFunds.create"));
        assertThat(subscribeToAccountRequest.getParams().getLedgerResourceEvent(), is(LedgerEventType.TRANSFER_CREATE));

        final String serializedJson = this.objectMapper.writeValueAsString(subscribeToAccountRequest);

        assertThat(serializedJson, is(SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_CREATE.replace(" ", "")));
    }

    private final String SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_UPDATE = " {" +
            "  \"id\": \"1\"," +
            "  \"jsonrpc\": \"2.0\"," +
            "  \"method\": \"subscribe_account\"," +
            "  \"params\": {" +
            "  \"event\": \"transferFunds.update\"," +
            "    \"accounts\": [\"https://ledger.example/accounts/alice\"]" +
            "   }" +
            "}";

    /**
     * Not specifying the event should subscribe the user to everything.
     */
    @Test
    public void JsonTest_TransferDotUpdate() throws Exception {
        final SubscribeToAccountRequest subscribeToAccountRequest = this.objectMapper.readValue(
                SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_UPDATE, SubscribeToAccountRequest.class);

        assertThat(subscribeToAccountRequest.getId(), is("1"));
        assertThat(subscribeToAccountRequest.getJsonrpc(), is("2.0"));
        assertThat(subscribeToAccountRequest.getMethod(), is("subscribe_account"));

        assertThat(subscribeToAccountRequest.getParams().getEventType().isPresent(), is(true));
        assertThat(subscribeToAccountRequest.getParams().getEventType().get(), is("transferFunds.update"));
        assertThat(subscribeToAccountRequest.getParams().getLedgerResourceEvent(), is(LedgerEventType.TRANSFER_UPDATE));

        final String serializedJson = this.objectMapper.writeValueAsString(subscribeToAccountRequest);

        assertThat(serializedJson, is(SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_UPDATE.replace(" ", "")));
    }

    private final String SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_STAR = " {" +
            "  \"id\": \"1\"," +
            "  \"jsonrpc\": \"2.0\"," +
            "  \"method\": \"subscribe_account\"," +
            "  \"params\": {" +
            "  \"event\": \"transferFunds.*\"," +
            "    \"accounts\": [\"https://ledger.example/accounts/alice\"]" +
            "   }" +
            "}";

    /**
     * Not specifying the event should subscribe the user to everything.
     */
    @Test
    public void JsonTest_TransferDotStar() throws Exception {
        final SubscribeToAccountRequest subscribeToAccountRequest = this.objectMapper.readValue(
                SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_STAR, SubscribeToAccountRequest.class);

        assertThat(subscribeToAccountRequest.getId(), is("1"));
        assertThat(subscribeToAccountRequest.getJsonrpc(), is("2.0"));
        assertThat(subscribeToAccountRequest.getMethod(), is("subscribe_account"));

        assertThat(subscribeToAccountRequest.getParams().getEventType().isPresent(), is(true));
        assertThat(subscribeToAccountRequest.getParams().getEventType().get(), is("transferFunds.*"));
        assertThat(subscribeToAccountRequest.getParams().getLedgerResourceEvent(), is(LedgerEventType.TRANSFER_ALL));

        final String serializedJson = this.objectMapper.writeValueAsString(subscribeToAccountRequest);

        assertThat(serializedJson, is(SUBSCRIBE_ACCOUNT_JSON__TRANSFER_DOT_STAR.replace(" ", "")));
    }

    private final String SUBSCRIBE_ACCOUNT_JSON__STAR = " {" +
            "  \"id\": \"1\"," +
            "  \"jsonrpc\": \"2.0\"," +
            "  \"method\": \"subscribe_account\"," +
            "  \"params\": {" +
            "  \"event\": \"*\"," +
            "    \"accounts\": [\"https://ledger.example/accounts/alice\"]" +
            "   }" +
            "}";

    /**
     * Not specifying the event should subscribe the user to everything.
     */
    @Test
    public void JsonTest_Transfer_Star() throws Exception {
        final SubscribeToAccountRequest subscribeToAccountRequest = this.objectMapper.readValue(
                SUBSCRIBE_ACCOUNT_JSON__STAR, SubscribeToAccountRequest.class);

        assertThat(subscribeToAccountRequest.getId(), is("1"));
        assertThat(subscribeToAccountRequest.getJsonrpc(), is("2.0"));
        assertThat(subscribeToAccountRequest.getMethod(), is("subscribe_account"));

        assertThat(subscribeToAccountRequest.getParams().getEventType().isPresent(), is(true));
        assertThat(subscribeToAccountRequest.getParams().getEventType().get(), is("*"));
        assertThat(subscribeToAccountRequest.getParams().getLedgerResourceEvent(), is(LedgerEventType.ALL));

        final String serializedJson = this.objectMapper.writeValueAsString(subscribeToAccountRequest);

        assertThat(serializedJson, is(SUBSCRIBE_ACCOUNT_JSON__STAR.replace(" ", "")));
    }
}