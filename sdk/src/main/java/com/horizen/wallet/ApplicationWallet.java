package com.horizen.wallet;

import java.util.List;

import com.horizen.proposition.Proposition;
import com.horizen.secret.Secret;
import com.horizen.box.Box;
import com.horizen.block.SidechainBlock;

public interface ApplicationWallet {

    void onAddSecret(Secret secret);
    void onRemoveSecret(Proposition proposition);
    void applyChanges(SidechainBlock block, List<Box<Proposition>> boxesToUpdate, List<byte[]> boxIdsToRemove, List<Box<Proposition>> coinsPoolWithdrawals);
    void onRollback(byte[] blockId);
}
