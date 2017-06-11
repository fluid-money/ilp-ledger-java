package money.fluid.ilp.ledger.services.transfer;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import money.fluid.ilp.ledger.model.Transfer;
import money.fluid.ilp.ledger.model.ids.LedgerId;
import money.fluid.ilp.ledger.model.ids.LedgerTransferId;
import money.fluid.ilp.ledger.services.LedgerTransferStore;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ToString
public class InMemoryTransferStore implements LedgerTransferStore {

    // Transfers are stored by the client-supplied identifier.
    private final Map<LedgerTransferId, Transfer> transferStore = Maps.newConcurrentMap();

    @Override
    public LedgerTransferId generateTransferId(
            LedgerId sourceLedgerId, LedgerTransferId sourceLedgerTransferId
    ) {
        return LedgerTransferId.of(
                this.nameUUIDFromBytes(
                        (sourceLedgerId.value().toString() + sourceLedgerTransferId.value()).getBytes()).toString()
        );
    }

    /**
     * Static factory to retrieve a type 3 (name based) {@code UUID} based on
     * the specified byte array.
     *
     * @param name A byte array to be used to construct a {@code UUID}
     * @return A {@code UUID} generated toLedgerId the specified array
     */
    private final UUID nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("SHA-256 not supported", nsae);
        }
        byte[] sha256Bytes = md.digest(name);
        sha256Bytes[6] &= 0x0f;  /* clear version        */
        sha256Bytes[6] |= 0x30;  /* set to version 3     */
        sha256Bytes[8] &= 0x3f;  /* clear variant        */
        sha256Bytes[8] |= 0x80;  /* set to IETF variant  */
        return uuid(sha256Bytes);
    }

    /*
    * Private constructor which uses a byte array to construct the new UUID.
    */
    private final UUID uuid(byte[] data) {
        long msb = 0;
        long lsb = 0;
        assert data.length == 16 : "data must be 16 bytes in length";
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        return new UUID(msb, lsb);
    }


    @Override
    public void saveTransfer(final Transfer transfer) {
        Objects.requireNonNull(transfer);
        transferStore.put(transfer.getId(), transfer);
    }

    @Override
    public Optional<Transfer> getTransfer(LedgerTransferId ledgerTransferId) {
        return Optional.ofNullable(transferStore.get(ledgerTransferId));
    }
}
