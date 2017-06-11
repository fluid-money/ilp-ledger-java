package money.fluid.ilp.ledger.web.websocket.model;

import money.fluid.ilp.ledger.exceptions.problems.messages.InvalidLedgerSubscriptionProblem;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LedgerResourceTypeEventTest {

//    @Test(expected = NullPointerException.class)
//    public void testParse_NullInput() throws Exception {
//        try {
//            LedgerResourceEvent.parseLedgerEventType(null);
//        } catch (NullPointerException e) {
//            throw e;
//        }
//    }
//
//    @Test(expected = InvalidLedgerSubscriptionProblem.class)
//    public void testParse_EmptyInput() throws Exception {
//        try {
//            LedgerResourceEvent.parseLedgerEventType(Optional.of(""));
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//
//    @Test(expected = InvalidLedgerSubscriptionProblem.class)
//    public void testParse_InvalidInput() throws Exception {
//        try {
//            LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable("foo"));
//        } catch (Exception e) {
//            throw e;
//        }
//    }
//
//    @Test
//    public void testParse__All() throws Exception {
//        final LedgerEventType event = LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable("*"));
//        assertThat(event, is(LedgerEventType.ALL));
//    }
//
//    @Test
//    public void testParse__Transfer_Dot_Star() throws Exception {
//        final LedgerEventType event = LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable("transferFunds.*"));
//        assertThat(event, is(LedgerEventType.ALL));
//    }
//
//    @Test
//    public void testParse__Transfer_Dot_Update() throws Exception {
//        final LedgerEventType event = LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable("transferFunds.update"));
//        assertThat(event, is(LedgerEventType.UPDATE));
//    }
//
//    @Test
//    public void testParse__Transfer_Dot_Create() throws Exception {
//        final LedgerEventType event = LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable("transferFunds.create"));
//        assertThat(event, is(LedgerEventType.CREATE));
//    }
//
//    @Test
//    public void testParse__Message_Dot_Send() throws Exception {
//        final LedgerEventType event = LedgerResourceEvent.parseLedgerEventType(Optional.ofNullable("message.send"));
//        assertThat(event, is(LedgerEventType.SEND));
//    }
}