package org.consenlabs.tokencore.wallet.chain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.consenlabs.tokencore.wallet.model.BIP44Util;
import org.consenlabs.tokencore.wallet.model.TokenException;
import org.consenlabs.tokencore.wallet.network.CustomBitcoinStyleNetParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 * Bulk-register chains from JSON. Intended for app-shipped catalogs or synced chainlists
 * (fetching remote JSON is left to the application).
 */
public final class ChainCatalogLoader {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ChainCatalogLoader() {
  }

  public static int registerAllFromJson(String json) {
    try {
      List<ChainCatalogEntry> entries = MAPPER.readValue(json, new TypeReference<List<ChainCatalogEntry>>() { });
      return registerAll(entries);
    } catch (IOException e) {
      throw new TokenException("Invalid chain catalog JSON", e);
    }
  }

  public static int registerAllFromStream(InputStream in) throws IOException {
    List<ChainCatalogEntry> entries = MAPPER.readValue(in, new TypeReference<List<ChainCatalogEntry>>() { });
    return registerAll(entries);
  }

  public static int registerAll(List<ChainCatalogEntry> entries) {
    ChainRegistry reg = ChainRegistry.getInstance();
    int n = 0;
    for (ChainCatalogEntry e : entries) {
      if (e.getChainType() == null || e.getFamily() == null) {
        throw new TokenException("chain catalog entry missing chainType or family");
      }
      String fam = e.getFamily().trim().toUpperCase(Locale.ROOT);
      switch (fam) {
        case "EVM":
          if (e.getChainId() == null) {
            throw new TokenException("EVM entry requires chainId: " + e.getChainType());
          }
          String evmPath = e.getDefaultMnemonicPath();
          if (evmPath == null || evmPath.trim().isEmpty()) {
            if (e.getSlip44() == null) {
              throw new TokenException("EVM entry requires defaultMnemonicPath or slip44: " + e.getChainType());
            }
            evmPath = BIP44Util.defaultEvmAccountZeroPath(e.getSlip44());
          }
          reg.registerEvm(new EvmChainRegistration(e.getChainType(), e.getChainId(), evmPath));
          n++;
          break;
        case "UTXO":
        case "BITCOIN_STYLE":
        case "BITCOIN_STYLE_UTXO":
          requireUtxoFields(e);
          boolean mainTpl = e.getUseMainnetDifficultyGenesis() == null || e.getUseMainnetDifficultyGenesis();
          CustomBitcoinStyleNetParams net = new CustomBitcoinStyleNetParams(
              "custom." + e.getChainType().trim().toLowerCase(Locale.ROOT),
              e.getAddressHeader(),
              e.getP2shHeader(),
              e.getDumpedPrivateKeyHeader(),
              e.getBip32HeaderPub(),
              e.getBip32HeaderPriv(),
              mainTpl);
          String utxoPath = e.getDefaultMnemonicPath();
          if (utxoPath == null || utxoPath.trim().isEmpty()) {
            if (e.getSlip44() == null) {
              throw new TokenException("UTXO entry requires defaultMnemonicPath or slip44: " + e.getChainType());
            }
            utxoPath = BIP44Util.defaultAccountZeroPath(e.getSlip44());
          }
          reg.registerUtxo(new UtxoChainRegistration(e.getChainType(), net, utxoPath));
          n++;
          break;
        case "TRON":
        case "FILECOIN":
        case "EOS":
          // Built-ins: catalog row is informational only
          break;
        default:
          throw new TokenException("Unknown family in catalog: " + fam);
      }
    }
    return n;
  }

  private static void requireUtxoFields(ChainCatalogEntry e) {
    if (e.getAddressHeader() == null
        || e.getP2shHeader() == null
        || e.getDumpedPrivateKeyHeader() == null
        || e.getBip32HeaderPub() == null
        || e.getBip32HeaderPriv() == null) {
      throw new TokenException("UTXO entry requires address headers: " + e.getChainType());
    }
  }
}
