package org.consenlabs.tokencore.wallet.model;

import com.google.common.collect.ImmutableList;
import org.bitcoinj.crypto.ChildNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BIP44UtilTest {

    @Test
    void generatePath_bitcoinMainnet() {
        ImmutableList<ChildNumber> path = BIP44Util.generatePath("m/44'/0'/0'");
        assertEquals(3, path.size());
        assertEquals(new ChildNumber(44, true), path.get(0));
        assertEquals(new ChildNumber(0, true), path.get(1));
        assertEquals(new ChildNumber(0, true), path.get(2));
    }

    @Test
    void generatePath_ethereumFull() {
        ImmutableList<ChildNumber> path = BIP44Util.generatePath("m/44'/60'/0'/0/0");
        assertEquals(5, path.size());
        assertEquals(new ChildNumber(44, true), path.get(0));
        assertEquals(new ChildNumber(60, true), path.get(1));
        assertEquals(new ChildNumber(0, true), path.get(2));
        assertEquals(new ChildNumber(0, false), path.get(3));
        assertEquals(new ChildNumber(0, false), path.get(4));
    }

    @Test
    void generatePath_tronPath() {
        ImmutableList<ChildNumber> path = BIP44Util.generatePath(BIP44Util.TRON_PATH);
        assertEquals(5, path.size());
        assertEquals(new ChildNumber(195, true), path.get(1));
    }

    @Test
    void getBTCMnemonicPath_segwitMainnet() {
        String path = BIP44Util.getBTCMnemonicPath(Metadata.P2WPKH, true);
        assertEquals(BIP44Util.BITCOIN_SEGWIT_MAIN_PATH, path);
    }

    @Test
    void getBTCMnemonicPath_segwitTestnet() {
        String path = BIP44Util.getBTCMnemonicPath(Metadata.P2WPKH, false);
        assertEquals(BIP44Util.BITCOIN_SEGWIT_TESTNET_PATH, path);
    }

    @Test
    void getBTCMnemonicPath_legacyMainnet() {
        String path = BIP44Util.getBTCMnemonicPath(Metadata.NONE, true);
        assertEquals(BIP44Util.BITCOIN_MAINNET_PATH, path);
    }

    @Test
    void getBTCMnemonicPath_legacyTestnet() {
        String path = BIP44Util.getBTCMnemonicPath(Metadata.NONE, false);
        assertEquals(BIP44Util.BITCOIN_TESTNET_PATH, path);
    }

    @Test
    void pathConstants_areCorrect() {
        assertTrue(BIP44Util.ETHEREUM_PATH.startsWith("m/44'/60'"));
        assertTrue(BIP44Util.TRON_PATH.startsWith("m/44'/195'"));
        assertTrue(BIP44Util.FILECOIN_PATH.startsWith("m/44'/461'"));
        assertTrue(BIP44Util.LITECOIN_MAINNET_PATH.startsWith("m/44'/2'"));
        assertTrue(BIP44Util.DOGECOIN_MAINNET_PATH.startsWith("m/44'/3'"));
        assertTrue(BIP44Util.DASH_MAINNET_PATH.startsWith("m/44'/5'"));
    }
}
