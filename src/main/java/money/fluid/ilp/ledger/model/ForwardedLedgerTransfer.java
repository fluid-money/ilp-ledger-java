package money.fluid.ilp.ledger.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import org.ilpx.core.IlpAddress;

import javax.money.MonetaryAmount;
import java.util.Optional;

@RequiredArgsConstructor
@Builder
@Getter
@ToString
@EqualsAndHashCode
public class ForwardedLedgerTransfer {

//    @NonNull
//    private final InterledgerPacketHeader interledgerPacketHeader;

    @NonNull
    private final LedgerId ledgerId;

    @NonNull
    private final IlpAddress localSourceAddress;

    @NonNull
    private final MonetaryAmount amount;

    @NonNull
    private final Optional<String> optData;

    @NonNull
    private final Optional<String> optNoteToSelf;
}
