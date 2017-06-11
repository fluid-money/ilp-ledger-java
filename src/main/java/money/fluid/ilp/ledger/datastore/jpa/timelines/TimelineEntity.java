package money.fluid.ilp.ledger.datastore.jpa.timelines;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.ZonedDateTime;

/**
 * Storage entity for a transferFunds's timeline.
 */
@Embeddable
@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class TimelineEntity {

    //@Id
    //private final String transferId;

    @Column(name = "CREATED_AT")
    private final ZonedDateTime createdAt;

    @Column(name = "EXECUTED_AT")
    private final ZonedDateTime executedAt;

    @Column(name = "REJECTED_AT")
    private final ZonedDateTime rejectedAt;

    /**
     * No-args Constructor.  Exists only for Hibernate...
     */
    TimelineEntity() {
        this.createdAt = null;
        this.executedAt = null;
        this.rejectedAt = null;
    }
}
