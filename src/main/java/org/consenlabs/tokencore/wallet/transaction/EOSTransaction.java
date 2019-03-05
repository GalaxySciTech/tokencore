package org.consenlabs.tokencore.wallet.transaction;

import io.eblock.eos4j.OfflineSign;
import io.eblock.eos4j.api.vo.SignParam;
import io.eblock.eos4j.ese.Action;
import io.eblock.eos4j.ese.DataParam;
import io.eblock.eos4j.ese.DataType;
import io.eblock.eos4j.utils.ByteUtils;
import org.consenlabs.tokencore.foundation.crypto.Hash;
import org.consenlabs.tokencore.foundation.utils.ByteUtil;
import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.WalletManager;
import org.consenlabs.tokencore.wallet.keystore.V3Keystore;
import org.consenlabs.tokencore.wallet.model.TokenException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EOSTransaction implements TransactionSigner {

    private byte[] txBuf;
    private List<ToSignObj> txsToSign;
    private String from;
    private String to;
    private String quantity;
    private String memo;
    private SignParam signParam;

    public EOSTransaction(byte[] txBuf) {
        this.txBuf = txBuf;
    }

    public EOSTransaction(List<ToSignObj> txsToSign) {
        this.txsToSign = txsToSign;
    }

    public EOSTransaction(SignParam signParam, String from, String to, String quantity, String memo) {
        this.from = from;
        this.to = to;
        this.quantity = quantity;
        this.memo = memo;
        this.signParam = signParam;
    }

    public TxSignResult signTransaction(String password, Wallet wallet) {
        String pubKey=wallet.getKeyPathPrivates().get(0).getPublicKey();
        String priKey = NumericUtil.bytesToHex(wallet.decryptPrvKeyFor(pubKey, password));
        OfflineSign sign = new OfflineSign();
        try {
            String content = sign.transfer(signParam, priKey, "eosio.token",
                    from, to, quantity, memo);
            return new TxSignResult(content, "");
        } catch (Exception e) {
            throw new TokenException(String.format("签名失败 原因:%s",e.getMessage()));
        }
    }


    @Deprecated
    @Override
    public TxSignResult signTransaction(String chainId, String password, Wallet wallet) {
        String transactionID = NumericUtil.bytesToHex(Hash.sha256(txBuf));
        txBuf = ByteUtil.concat(NumericUtil.hexToBytes(chainId), txBuf);

        byte[] zeroBuf = new byte[32];
        Arrays.fill(zeroBuf, (byte) 0);
        txBuf = ByteUtil.concat(txBuf, zeroBuf);

        String wif = wallet.exportPrivateKey(password);
        String signed = EOSSign.sign(Hash.sha256(txBuf), wif);

        return new TxSignResult(signed, transactionID);
    }

    public List<TxMultiSignResult> signTransactions(String chainId, String password, Wallet wallet) {
        List<TxMultiSignResult> results = new ArrayList<>(txsToSign.size());
        for (ToSignObj toSignObj : txsToSign) {

            byte[] txBuf = NumericUtil.hexToBytes(toSignObj.txHex);
            String transactionID = NumericUtil.bytesToHex(Hash.sha256(txBuf));

            byte[] txChainIDBuf = ByteUtil.concat(NumericUtil.hexToBytes(chainId), txBuf);

            byte[] zeroBuf = new byte[32];
            Arrays.fill(zeroBuf, (byte) 0);
            byte[] fullTxBuf = ByteUtil.concat(txChainIDBuf, zeroBuf);

            byte[] hashedTx = Hash.sha256(fullTxBuf);

            List<String> signatures = new ArrayList<>(toSignObj.publicKeys.size());
            for (String pubKey : toSignObj.publicKeys) {
                String signed;
                if (wallet.getKeystore().getVersion() == V3Keystore.VERSION) {
                    signed = EOSSign.sign(hashedTx, wallet.exportPrivateKey(password));
                } else {
                    signed = EOSSign.sign(hashedTx, wallet.decryptPrvKeyFor(pubKey, password));
                }

                signatures.add(signed);
            }

            TxMultiSignResult signedResult = new TxMultiSignResult(transactionID, signatures);
            results.add(signedResult);
        }
        return results;
    }

    public static class ToSignObj {
        private String txHex;
        private List<String> publicKeys;

        public String getTxHex() {
            return txHex;
        }

        public void setTxHex(String txHex) {
            this.txHex = txHex;
        }

        public List<String> getPublicKeys() {
            return publicKeys;
        }

        public void setPublicKeys(List<String> publicKeys) {
            this.publicKeys = publicKeys;
        }
    }
}
