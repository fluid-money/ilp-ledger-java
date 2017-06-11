package money.fluid.ilp.ledger.datastore.jpa;

import money.fluid.ilp.ledger.config.Application;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TimelineEntity;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferEntity;
import money.fluid.ilp.ledger.datastore.jpa.timelines.TransferRepository;
import money.fluid.ilp.ledger.model.Transfer.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * Unit tests for {@link TransferRepository}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TransferRepositoryTest {

    @Autowired
    private TransferRepository transferRepository;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void createTransfer() {
        final TimelineEntity timelineEntity = TimelineEntity.builder()
                .createdAt(ZonedDateTime.now(ZoneId.of("Z")))
                .build();

        final TransferEntity transferEntity = TransferEntity.builder()
                .debitAccountId("debitAccount")
                .creditAccountId("creditAccount")
                .amount(new BigDecimal("5.00"))
                .status(Status.PREPARED)
                .currencyCode("USD")

                // TODO: ExecutionCondition and expiresAt

                .timeline(timelineEntity)
                .memo("memo")
                .additionalInfo("additional info")
                .noteToSelf("note to self")

                .build();
        this.transferRepository.save(transferEntity);

        final TransferEntity loadedTransferEntity = this.transferRepository.findOne(transferEntity.getId());
        assertThat(loadedTransferEntity, is(transferEntity));
    }
}