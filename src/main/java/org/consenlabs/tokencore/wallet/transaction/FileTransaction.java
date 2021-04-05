package org.consenlabs.tokencore.wallet.transaction;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import com.filecoinj.crypto.ECKey;
import com.filecoinj.handler.TransactionHandler;
import com.filecoinj.model.Transaction;
import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.model.Messages;
import org.consenlabs.tokencore.wallet.model.TokenException;

public class FileTransaction implements TransactionSigner {

    private String from;

    private String to;

    private long amount;

    private long nonce;

    private String gasPremium;

    private long gasLimit;

    private String gasFeeCap;

    private final static long DUST_THRESHOLD = 0;

    public FileTransaction(String from, String to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        if (amount < DUST_THRESHOLD) {
            throw new TokenException(Messages.AMOUNT_LESS_THAN_MINIMUM);
        }
    }

    @Override
    public TxSignResult signTransaction(String chainId, String password, Wallet wallet) {
        try {
            String hexPrivateKey = wallet.exportPrivateKey(password);
            Transaction transaction = Transaction.builder()
                    .from(from)
                    .to(to)
                    .gasFeeCap(gasFeeCap)
                    .gasLimit(gasLimit)
                    .gasPremium(gasPremium)
                    .method(0L)
                    .nonce(nonce)
                    .params("")
                    .value(String.valueOf(amount)).build();
            TransactionHandler transactionHandler = new TransactionHandler();
            byte[] cidHash = transactionHandler.transactionSerialize(transaction);

            String cid = HexUtil.encodeHexStr(cidHash);
            ECKey ecKey = ECKey.fromPrivate(HexUtil.decodeHex(hexPrivateKey));
            String sing = Base64.encode(ecKey.sign(cidHash).toByteArray());
            System.out.println("cidHash: " + HexUtil.encodeHexStr(cidHash));
            return new TxSignResult(sing, cid);
        } catch (Exception e) {
            throw new TokenException("签名失败 原因", e);
        }
    }
}
