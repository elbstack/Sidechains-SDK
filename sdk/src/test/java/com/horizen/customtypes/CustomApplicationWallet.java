package com.horizen.customtypes;

import com.horizen.box.Box;
import com.horizen.proposition.Proposition;
import com.horizen.secret.Secret;
import com.horizen.wallet.ApplicationWallet;
import com.horizen.block.SidechainBlock;

import java.util.List;

public class CustomApplicationWallet implements ApplicationWallet {
    @Override
    public void onAddSecret(Secret secret) {

    }

    @Override
    public void onRemoveSecret(Proposition proposition) {

    }

    @Override
    public void applyChanges(SidechainBlock block, List<Box<Proposition>> boxesToUpdate, List<byte[]> boxIdsToRemove, List<Box<Proposition>> coinsPoolWithdrawals) {

    }

    @Override
    public void onRollback(byte[] blockId) {

    }
}
