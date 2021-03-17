package org.consenlabs.tokencore.wallet.transaction;


import org.consenlabs.tokencore.wallet.Wallet;

public class TronTransaction implements TransactionSigner {


    @Override
    public TxSignResult signTransaction(String chainId, String password, Wallet wallet) {

        return null;
    }

    public TxSignResult signTrc20Transaction(String chainId, String password, Wallet wallet) {

        return null;
    }

}

