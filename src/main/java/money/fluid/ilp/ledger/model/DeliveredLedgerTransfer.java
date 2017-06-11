package money.fluid.ilp.ledger.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.ilpx.core.IlpAddress;

import javax.money.MonetaryAmount;
import java.util.Optional;

@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class DeliveredLedgerTransfer {

//    @NonNull
//    private final InterledgerPacketHeader interledgerPacketHeader;

    @NonNull
    private final IlpAddress localSourceAddress;

    @NonNull
    private final IlpAddress localDestinationAddress;

    @NonNull
    private final MonetaryAmount amount;

    @NonNull
    private final Optional<String> optData;

    @NonNull
    private final Optional<String> optNoteToSelf;

//    /**
//     * For a delivered transferFunds, the ledgerId is the ledgerId of the destination account address.
//     */
//    public IlpPrefix getLedgerId() {
//        return this.getLocalDestinationAddress().getLedgerPrefix();
//    }
}
