package org.consenlabs.tokencore.wallet.transaction;


import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.model.Messages;
import org.consenlabs.tokencore.wallet.model.TokenException;
import org.spongycastle.util.encoders.Hex;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.crypto.SECP256K1;
import org.tron.tronj.crypto.tuweniTypes.Bytes32;
import org.tron.tronj.proto.Chain;
import org.tron.tronj.proto.Response;
import org.tron.tronj.abi.FunctionEncoder;
import org.tron.tronj.abi.TypeReference;
import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.Bool;
import org.tron.tronj.abi.datatypes.Function;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.api.GrpcAPI.EmptyMessage;
import org.tron.tronj.proto.Chain.Transaction;
import org.tron.tronj.proto.Contract.TriggerSmartContract;
import org.tron.tronj.proto.Response.BlockExtention;
import org.tron.tronj.proto.Response.TransactionExtention;
import org.tron.tronj.proto.Response.TransactionReturn;

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

    TronClient client = TronClient.ofMainnet("3333333333333333333333333333333333333333333333333333333333333333");

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

        TriggerSmartContract trigger =
                TriggerSmartContract.newBuilder()
                        .setOwnerAddress(TronClient.parseAddress(from))
                        .setContractAddress(TronClient.parseAddress(contractAddress))
                        .setData(TronClient.parseHex(encodedHex))
                        .build();

        TransactionExtention txnExt = client.blockingStub.triggerContract(trigger);
        String txid = Hex.toHexString(txnExt.getTxid().toByteArray());

        Transaction signedTxn = client.signTransaction(txnExt, keyPair);

        return new TxSignResult(signedTxn.toString(), txid);
    }

}

