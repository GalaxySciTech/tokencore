package org.consenlabs.tokencore.wallet.chain;

import org.consenlabs.tokencore.wallet.address.AddressCreatorManager;
import org.consenlabs.tokencore.wallet.model.BIP44Util;
import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.TokenException;
import org.consenlabs.tokencore.wallet.network.CustomBitcoinStyleNetParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChainRegistryTest {

  @Test
  void registerEvm_thenValidateAndAddressCreatorWork() {
    String ct = "TESTEVM_" + System.nanoTime();
    ChainRegistry.getInstance()
        .registerEvm(new EvmChainRegistration(ct, 999999L, "m/44'/1'/0'/0/0"));

    assertDoesNotThrow(() -> org.consenlabs.tokencore.wallet.model.ChainType.validate(ct));

    String addr =
        AddressCreatorManager.getInstance(ct, true, Metadata.NONE)
            .fromPrivateKey(
                "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c");
    assertEquals(40, addr.length());
  }

  @Test
  void registerUtxo_thenAddressCreatorDerivesBase58() {
    String ct = "TESTUTXO_" + System.nanoTime();
    CustomBitcoinStyleNetParams net =
        new CustomBitcoinStyleNetParams(
            "custom.testutxo." + ct,
            0x30,
            0x16,
            0x9e,
            0x0488b21e,
            0x0488ade4,
            true);
    ChainRegistry.getInstance()
        .registerUtxo(new UtxoChainRegistration(ct, net, "m/44'/3'/0'"));

    String addr =
        AddressCreatorManager.getInstance(ct, true, Metadata.NONE)
            .fromPrivateKey(
                "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c");
    assertTrue(addr.length() > 20);
  }

  @Test
  void chainCatalogLoader_registersEvmFromJson() {
    String ct = "CAT_" + System.nanoTime();
    String json =
        "[{\"chainType\":\""
            + ct
            + "\",\"family\":\"EVM\",\"chainId\":424242,\"slip44\":1}]";
    int n = ChainCatalogLoader.registerAllFromJson(json);
    assertEquals(1, n);
    assertDoesNotThrow(() -> ChainType.validate(ct));
    assertEquals("m/44'/1'/0'/0/0", ChainRegistry.getInstance().getDefaultMnemonicPath(ct));
  }

  @Test
  void resolveFamily_unknownThrows() {
    assertThrows(TokenException.class, () -> ChainRegistry.getInstance().resolveFamily("NOT_A_CHAIN_" + System.nanoTime()));
  }

  @Test
  void ethereumBuiltin_chainId() {
    assertEquals(1L, ChainRegistry.getInstance().getEvmChainId(ChainType.ETHEREUM));
  }
}
