package org.consenlabs.tokencore.wallet.transaction;


import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.model.Messages;
import org.consenlabs.tokencore.wallet.model.TokenException;
import org.spongycastle.util.encoders.Hex;
import org.tron.trident.abi.FunctionEncoder;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Bool;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.exceptions.IllegalException;
import org.tron.trident.crypto.SECP256K1;
import org.tron.trident.crypto.tuwenitypes.Bytes32;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Contract;
import org.tron.trident.proto.Response;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;


public class TronTransaction implements TransactionSigner {

    private final static long DUST_THRESHOLD = 0;

    public TronTransaction(String from, String to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        if (amount < DUST_THRESHOLD) {
            throw new TokenException(Messages.AMOUNT_LESS_THAN_MINIMUM);
        }
    }

    public TronTransaction(String from, String to, int tokenId, long amount) {
        this.from = from;
        this.to = to;
        this.tokenId = tokenId;
        this.amount = amount;
        if (amount < DUST_THRESHOLD) {
            throw new TokenException(Messages.AMOUNT_LESS_THAN_MINIMUM);
        }
    }

    public TronTransaction(String from, String to, String contractAddress, long amount) {
        this.from = from;
        this.to = to;
        this.contractAddress = contractAddress;
        this.amount = amount;
        if (amount < DUST_THRESHOLD) {
            throw new TokenException(Messages.AMOUNT_LESS_THAN_MINIMUM);
        }
    }

    private String from;

    private String to;

    private long amount;

    private int tokenId;

    private String contractAddress;

    ApiWrapper client = ApiWrapper.ofMainnet("");

    @Override
    public TxSignResult signTransaction(String chainId, String password, Wallet wallet) {

        String hexPrivateKey = wallet.exportPrivateKey(password);
        SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey)));
        Response.TransactionExtention txnExt;
        try {

            txnExt = client.transfer(from, to, amount);
            Chain.Transaction signedTransaction = client.signTransaction(txnExt, keyPair);
            String txid = Hex.toHexString(txnExt.getTxid().toByteArray());
            return new TxSignResult(signedTransaction.toString(), txid);
        } catch (IllegalException e) {
            throw new TokenException("签名失败 原因", e);
        }
    }

    public TxSignResult signTrc10Transaction(String chainId, String password, Wallet wallet) {
        String hexPrivateKey = wallet.exportPrivateKey(password);
        SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey)));
        Response.TransactionExtention txnExt;
        try {
            txnExt = client.transferTrc10(from, to, tokenId, amount);
            Chain.Transaction signedTransaction = client.signTransaction(txnExt, keyPair);
            String txid = Hex.toHexString(txnExt.getTxid().toByteArray());
            return new TxSignResult(signedTransaction.toString(), txid);
        } catch (IllegalException e) {
            throw new TokenException("签名失败 原因", e);
        }
    }


    public TxSignResult signTrc20Transaction(String chainId, String password, Wallet wallet) {
        String hexPrivateKey = wallet.exportPrivateKey(password);
        SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey)));
        // transfer(address,uint256) returns (bool)
        Function trc20Transfer = new Function("transfer",
                Arrays.asList(new Address(to),
                        new Uint256(BigInteger.valueOf(amount))),
                Collections.singletonList(new TypeReference<Bool>() {
                }));

        String encodedHex = FunctionEncoder.encode(trc20Transfer);

        Contract.TriggerSmartContract trigger =
                Contract.TriggerSmartContract.newBuilder()
                        .setOwnerAddress(ApiWrapper.parseAddress(from))
                        .setContractAddress(ApiWrapper.parseAddress(contractAddress))
                        .setData(ApiWrapper.parseHex(encodedHex))
                        .build();

        Response.TransactionExtention txnExt = client.blockingStub.triggerContract(trigger);
        String txid = Hex.toHexString(txnExt.getTxid().toByteArray());

        Chain.Transaction signedTxn = client.signTransaction(txnExt, keyPair);

        return new TxSignResult(signedTxn.toString(), txid);
    }

}


