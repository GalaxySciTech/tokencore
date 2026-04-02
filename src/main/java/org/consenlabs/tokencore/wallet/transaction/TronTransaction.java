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

    private volatile ApiWrapper client;

    public TronTransaction setClient(ApiWrapper client) {
        this.client = client;
        return this;
    }

    private ApiWrapper getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = ApiWrapper.ofMainnet("");
                }
            }
        }
        return client;
    }

    @Override
    public TxSignResult signTransaction(String chainId, String password, Wallet wallet) {
        String hexPrivateKey = wallet.exportPrivateKey(password);
        SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey)));
        try {
            ApiWrapper api = getClient();
            Response.TransactionExtention txnExt = api.transfer(from, to, amount);
            Chain.Transaction signedTransaction = api.signTransaction(txnExt, keyPair);
            String txid = Hex.toHexString(txnExt.getTxid().toByteArray());
            return new TxSignResult(signedTransaction.toString(), txid);
        } catch (IllegalException e) {
            throw new TokenException("Tron sign failed", e);
        }
    }

    public TxSignResult signTrc10Transaction(String chainId, String password, Wallet wallet) {
        String hexPrivateKey = wallet.exportPrivateKey(password);
        SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey)));
        try {
            ApiWrapper api = getClient();
            Response.TransactionExtention txnExt = api.transferTrc10(from, to, tokenId, amount);
            Chain.Transaction signedTransaction = api.signTransaction(txnExt, keyPair);
            String txid = Hex.toHexString(txnExt.getTxid().toByteArray());
            return new TxSignResult(signedTransaction.toString(), txid);
        } catch (IllegalException e) {
            throw new TokenException("Tron TRC10 sign failed", e);
        }
    }

    public TxSignResult signTrc20Transaction(String chainId, String password, Wallet wallet) {
        String hexPrivateKey = wallet.exportPrivateKey(password);
        SECP256K1.KeyPair keyPair = SECP256K1.KeyPair.create(SECP256K1.PrivateKey.create(Bytes32.fromHexString(hexPrivateKey)));

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

        ApiWrapper api = getClient();
        Response.TransactionExtention txnExt = api.blockingStub.triggerContract(trigger);
        String txid = Hex.toHexString(txnExt.getTxid().toByteArray());
        Chain.Transaction signedTxn = api.signTransaction(txnExt, keyPair);

        return new TxSignResult(signedTxn.toString(), txid);
    }

}


