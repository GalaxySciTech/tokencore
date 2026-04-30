package org.consenlabs.tokencore.wallet;

import org.consenlabs.tokencore.foundation.utils.MnemonicUtil;
import org.consenlabs.tokencore.wallet.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WalletManagerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        WalletManager.storage = () -> tempDir.toFile();
        WalletManager.clearKeystoreMap();
        Identity.currentIdentity = null;
    }

    @Test
    void scanWallets_emptyDirectory_shouldNotThrow() {
        assertDoesNotThrow(() -> WalletManager.scanWallets());
        assertTrue(WalletManager.getKeyMap().isEmpty());
    }

    @Test
    void createIdentity_shouldCreateWallets() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        assertNotNull(identity);
        assertNotNull(identity.getIdentifier());
        assertFalse(identity.getWallets().isEmpty());
    }

    @Test
    void deriveEthereumWallet_shouldReturnValidAddress() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet wallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        assertNotNull(wallet);
        assertNotNull(wallet.getAddress());
        assertEquals(40, wallet.getAddress().length());
    }

    @Test
    void deriveBitcoinWallet_shouldReturnValidAddress() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet wallet = identity.deriveWalletByMnemonics(
                ChainType.BITCOIN, "password123", MnemonicUtil.randomMnemonicCodes());

        assertNotNull(wallet);
        assertNotNull(wallet.getAddress());
    }

    @Test
    void findWalletByAddress_shouldReturnWallet() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        Wallet found = WalletManager.findWalletByAddress(
                ChainType.ETHEREUM, ethWallet.getAddress());
        assertNotNull(found);
        assertEquals(ethWallet.getAddress(), found.getAddress());
    }


    @Test
    void findWalletByKeystore_ethereum_shouldReturnWallet() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        String keystore = WalletManager.exportKeystore(ethWallet.getId(), "password123");
        Wallet found = WalletManager.findWalletByKeystore(ChainType.ETHEREUM, keystore, "password123");

        assertNotNull(found);
        assertEquals(ethWallet.getAddress(), found.getAddress());
    }



    @Test
    void findWalletByMnemonic_dogecoin_shouldReturnWallet() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.NONE);

        List<String> mnemonics = MnemonicUtil.randomMnemonicCodes();
        Wallet dogeWallet = identity.deriveWalletByMnemonics(
                ChainType.DOGECOIN, "password123", mnemonics);

        String mnemonic = String.join(" ", mnemonics);
        Wallet found = WalletManager.findWalletByMnemonic(
                ChainType.DOGECOIN,
                Network.MAINNET,
                mnemonic,
                BIP44Util.DOGECOIN_MAINNET_PATH,
                Metadata.NONE);

        assertNotNull(found);
        assertEquals(dogeWallet.getAddress(), found.getAddress());
    }

    @Test
    void findWalletByAddress_notFound_shouldReturnNull() {
        Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet found = WalletManager.findWalletByAddress(
                ChainType.ETHEREUM, "nonexistentaddress");
        assertNull(found);
    }

    @Test
    void exportPrivateKey_shouldReturnHexKey() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        String privateKey = WalletManager.exportPrivateKey(ethWallet.getId(), "password123");
        assertNotNull(privateKey);
        assertTrue(privateKey.length() > 0);
    }

    @Test
    void exportMnemonic_shouldReturnMnemonicAndPath() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        MnemonicAndPath result = WalletManager.exportMnemonic(ethWallet.getId(), "password123");
        assertNotNull(result);
        assertNotNull(result.getMnemonic());
        String[] words = result.getMnemonic().split(" ");
        assertEquals(12, words.length);
    }

    @Test
    void changePassword_shouldWork() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        assertDoesNotThrow(() ->
                WalletManager.changePassword(ethWallet.getId(), "password123", "newpass456"));
    }

    @Test
    void removeWallet_shouldRemoveFromMap() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        String walletId = ethWallet.getId();
        assertNotNull(WalletManager.findWalletById(walletId));

        WalletManager.removeWallet(walletId, "password123");
        assertNull(WalletManager.findWalletById(walletId));
    }

    @Test
    void removeWallet_wrongPassword_shouldThrow() {
        Identity identity = Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Wallet ethWallet = identity.deriveWalletByMnemonics(
                ChainType.ETHEREUM, "password123", MnemonicUtil.randomMnemonicCodes());

        assertThrows(TokenException.class, () ->
                WalletManager.removeWallet(ethWallet.getId(), "wrongpassword"));
    }

    @Test
    void mustFindWalletById_nonExistent_shouldThrow() {
        assertThrows(TokenException.class, () ->
                WalletManager.mustFindWalletById("nonexistent-id"));
    }

    @Test
    void importWalletFromPrivateKey_ethereum() {
        Identity.createIdentity(
                "test", "password123", "", Network.MAINNET, Metadata.P2WPKH);

        Metadata metadata = new Metadata();
        metadata.setChainType(ChainType.ETHEREUM);
        metadata.setSource(Metadata.FROM_PRIVATE);

        String privKey = "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c";
        Wallet wallet = WalletManager.importWalletFromPrivateKey(
                metadata, privKey, "password123", true);

        assertNotNull(wallet);
        assertNotNull(wallet.getAddress());
        assertEquals(40, wallet.getAddress().length());
    }
}
