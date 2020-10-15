package com.horizen.transaction;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.horizen.box.NoncedBox;
import com.horizen.box.BoxUnlocker;
import com.horizen.proposition.Proposition;
import com.horizen.utils.BytesUtils;
import scorex.crypto.hash.Blake2b256;

import java.io.ByteArrayOutputStream;
import java.util.List;

abstract public class SidechainTransaction<P extends Proposition, B extends NoncedBox<P>> extends BoxTransaction<P, B>
{
    // We don't need to calculate the hashWithoutNonce value each time, because transaction is immutable.
    private byte[] _hashWithoutNonce;

    private synchronized byte[] hashWithoutNonce() {
        if(_hashWithoutNonce == null) {
            ByteArrayOutputStream unlockersStream = new ByteArrayOutputStream();
            for (BoxUnlocker<P> u : unlockers()){
                unlockersStream.write(u.closedBoxId(), 0, u.closedBoxId().length);
            }

            ByteArrayOutputStream newBoxesPropositionsStream = new ByteArrayOutputStream();
            for (P proposition : newBoxesPropositions()){
                newBoxesPropositionsStream.write(proposition.bytes(), 0, proposition.bytes().length);
            }

            _hashWithoutNonce = Blake2b256.hash(
                    Bytes.concat(
                            unlockersStream.toByteArray(),
                            newBoxesPropositionsStream.toByteArray(),
                            Longs.toByteArray(timestamp()),
                            Longs.toByteArray(fee())
                    )
            );
        }
        return _hashWithoutNonce;
    }

    // Declaring the same rule for nonce calculation for all SidechainTransaction inheritors
    protected final long getNewBoxNonce(P newBoxProposition, int newBoxIndex) {
        byte[] hash = Blake2b256.hash(
                Bytes.concat(
                        newBoxProposition.bytes(),
                        hashWithoutNonce(),
                        Ints.toByteArray(newBoxIndex)
                )
        );
        return BytesUtils.getLong(hash, 0);
    }

    public abstract List<P> newBoxesPropositions();

    public abstract boolean transactionSemanticValidity();

    // We check, that nonces for new boxes were enforced according to our algorithm. Then do inheritors check.
    @Override
    public final boolean semanticValidity() {
        // Check that transaction doesn't make a double spend,
        // by verifying that the the size of the unique set of box ids is equal to the unlockers size.
        if(unlockers().size() != boxIdsToOpen().size())
            return false;

        // Check output boxes nonce correctness.
        List<B> boxes = newBoxes();
        for(int i = 0; i < boxes.size(); i++) {
            if(boxes.get(i).nonce() != getNewBoxNonce(boxes.get(i).proposition(), i)) {
                return false;
            }
        }
        return transactionSemanticValidity();
    }
}