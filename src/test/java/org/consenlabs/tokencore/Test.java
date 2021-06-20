package org.consenlabs.tokencore;

import cn.hutool.core.codec.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.consenlabs.tokencore.foundation.utils.MnemonicUtil;
import org.consenlabs.tokencore.wallet.Identity;
import org.consenlabs.tokencore.wallet.KeystoreStorage;
import org.consenlabs.tokencore.wallet.Wallet;
import org.consenlabs.tokencore.wallet.WalletManager;
import org.consenlabs.tokencore.wallet.model.ChainId;
import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.Network;
import org.consenlabs.tokencore.wallet.transaction.BitcoinTransaction;
import org.consenlabs.tokencore.wallet.transaction.TronTransaction;
import org.consenlabs.tokencore.wallet.transaction.TxSignResult;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.exceptions.IllegalException;
import org.tron.tronj.proto.Chain;
import org.tron.tronj.proto.Response;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Test implements KeystoreStorage {

    static String path = "/tmp";

    @Override
    public File getKeystoreDir() {
        return new File(path);
    }

    static public void init() {
        try {
            Files.createDirectories(Paths.get(path + "/wallets"));
        } catch (Throwable ignored) {
        }
        //KeystoreStorage是接口，实现它的getdir方法
        WalletManager.storage = new Test();
        WalletManager.scanWallets();
        String password = "123456";
        Identity identity = Identity.getCurrentIdentity();
        if (identity == null) {
            Identity.createIdentity(
                    "token",
                    password,
                    "",
                    Network.MAINNET,
                    Metadata.P2WPKH
            );
        }
    }


    static public void genBitcoinWallet() {
        init();
        Identity identity = Identity.getCurrentIdentity();
        String password = "123456";
        Wallet wallet = identity.deriveWalletByMnemonics(
                ChainType.BITCOIN,
                password,
                MnemonicUtil.randomMnemonicCodes()
        );
        System.out.println(wallet.getAddress());
    }

    static public void genFilecoinWallet() {
        init();
        Identity identity = Identity.getCurrentIdentity();
        String password = "123456";
        Wallet wallet = identity.deriveWalletByMnemonics(
                ChainType.FILECOIN,
                password,
                MnemonicUtil.randomMnemonicCodes()
        );
        System.out.println(wallet.getAddress());
        String privateKey=wallet.exportPrivateKey("123456");
        System.out.println(privateKey);
    }

    static public void signBitcoinTx() {
        init();
        String password = "123456";
        String toAddress = "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9";
        int changeIdx = 0;
        long amount = 1000L;
        long fee = 555L;
        //utxos需要去节点或者外部api获取
        ArrayList<BitcoinTransaction.UTXO> utxos = new ArrayList();
        BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(
                toAddress,
                changeIdx,
                amount,
                fee,
                utxos
        );
        Wallet wallet = WalletManager.findWalletByAddress(ChainType.BITCOIN, "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9");
        TxSignResult txSignResult = bitcoinTransaction.signTransaction(
                String.valueOf(ChainId.BITCOIN_MAINNET),
                password,
                wallet
        );
        System.out.println(txSignResult);
    }

    static public void signTrxTx() {
        init();
        String from = "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
        String to = "TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";
        long amount = 1;
        String password = "123456";
        Wallet wallet = WalletManager.findWalletByAddress(ChainType.BITCOIN, "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8");
        TronTransaction transaction = new TronTransaction(from, to, amount);
        //离线签名，不建议签名和广播放一块
        TxSignResult txSignResult = transaction.signTransaction(String.valueOf(ChainId.BITCOIN_MAINNET), password, wallet);

        System.out.println(txSignResult);
    }

    public static void main(String[] args) throws IllegalException {

        TronClient tronClient=TronClient.ofMainnet("");
        Response.TransactionInfo tx=tronClient.getTransactionInfoById("f3a54e8418edb5a91772e3fe6768a7a4e55fc6c33f212641c239589d8a453a58");
    }

}
