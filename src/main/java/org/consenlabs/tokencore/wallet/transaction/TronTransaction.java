package org.consenlabs.tokencore.wallet.transaction;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import org.consenlabs.tokencore.wallet.Wallet;
import org.tron.common.crypto.ECKey;
import org.tron.common.crypto.Sha256Sm3Hash;
import org.tron.common.utils.ByteArray;
import org.tron.common.utils.TransactionUtils;
import org.tron.protos.Protocol;
import org.tron.protos.contract.BalanceContract;
import org.tron.walletserver.WalletApi;

public class TronTransaction implements TransactionSigner {

    private final String from;

    private final String to;

    private final Long amount;



    public TronTransaction(String from, String to, Long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public static Protocol.Transaction setReference(Protocol.Transaction transaction,Protocol.Block newestBlock) {
        long blockHeight = newestBlock.getBlockHeader().getRawData().getNumber();

        byte[] blockHash = getBlockHash(newestBlock).getBytes();
        byte[] refBlockNum = ByteArray.fromLong(blockHeight);
        Protocol.Transaction.raw rawData = transaction.getRawData().toBuilder()
                .setRefBlockHash(ByteString.copyFrom(ByteArray.subArray(blockHash, 8, 16)))
                .setRefBlockBytes(ByteString.copyFrom(ByteArray.subArray(refBlockNum, 6, 8)))
                .build();
        return transaction.toBuilder().setRawData(rawData).build();
    }

    public static Sha256Sm3Hash getBlockHash(Protocol.Block block) {
        return Sha256Sm3Hash.of(block.getBlockHeader().getRawData().toByteArray());
    }

    public  String getTransactionHash(Protocol.Transaction transaction) {
        return ByteArray.toHexString(Sha256Sm3Hash.hash(transaction.getRawData().toByteArray()));
    }

    public static Protocol.Transaction createTransaction(byte[] from, byte[] to, long amount) {
        Protocol.Block newestBlock=WalletApi.getBlock(-1);
        Protocol.Transaction.Builder transactionBuilder = Protocol.Transaction.newBuilder();
        Protocol.Transaction.Contract.Builder contractBuilder = Protocol.Transaction.Contract.newBuilder();
        BalanceContract.TransferContract.Builder transferContractBuilder = BalanceContract.TransferContract.newBuilder();
        transferContractBuilder.setAmount(amount);
        ByteString bsTo = ByteString.copyFrom(to);
        ByteString bsOwner = ByteString.copyFrom(from);
        transferContractBuilder.setToAddress(bsTo);
        transferContractBuilder.setOwnerAddress(bsOwner);
        try {
            Any any = Any.pack(transferContractBuilder.build());
            contractBuilder.setParameter(any);
        } catch (Exception e) {
            return null;
        }
        contractBuilder.setType(Protocol.Transaction.Contract.ContractType.TransferContract);
        transactionBuilder.getRawDataBuilder().addContract(contractBuilder)
                .setTimestamp(System.currentTimeMillis())
                .setExpiration(newestBlock.getBlockHeader().getRawData().getTimestamp() + 10 * 60 * 60 * 1000);
        Protocol.Transaction transaction = transactionBuilder.build();
        return setReference(transaction, newestBlock);
    }


    @Override
    public TxSignResult signTransaction(String chainId, String password, Wallet wallet) {
        String privateStr = wallet.exportPrivateKey(password);
        byte[] privateBytes = ByteArray.fromHexString(privateStr);
        ECKey ecKey = ECKey.fromPrivate(privateBytes);
        Protocol.Transaction transaction = createTransaction(WalletApi.decodeFromBase58Check(from), WalletApi.decodeFromBase58Check(to), amount);
        assert transaction != null;
        transaction = TransactionUtils.sign(transaction, ecKey);
        String txHash=getTransactionHash(transaction);
        return new TxSignResult(ByteArray.toHexString(transaction.toByteArray()), txHash);
    }

    public static void main(String[] args) {

        String from="TGA1KxMbBvYWL2C4iirBPMz3Lt1BFoHB1D";
        String to="TGA1KxMbBvYWL2C4iirBPMz3Lt1BFoHB1D";
        long amount=0L;
        Protocol.Transaction transaction = createTransaction(WalletApi.decodeFromBase58Check(from), WalletApi.decodeFromBase58Check(to), amount);
        assert transaction != null;
        System.out.println(ByteArray.toHexString(transaction.toByteArray()));


    }
}

