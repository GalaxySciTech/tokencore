package org.consenlabs.tokencore.wallet.chain;

import com.google.common.collect.ImmutableSet;
import org.consenlabs.tokencore.wallet.model.BIP44Util;
import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.TokenException;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry for dynamically added EVM and Bitcoin-style UTXO chains.
 * Built-in {@link ChainType} constants remain valid without explicit registration.
 */
public final class ChainRegistry {

  private static final ChainRegistry INSTANCE = new ChainRegistry();

  private static final Set<String> BITCOIN_STYLE_BUILTIN = ImmutableSet.of(
      ChainType.BITCOIN,
      ChainType.LITECOIN,
      ChainType.DASH,
      ChainType.DOGECOIN,
      ChainType.BITCOINCASH,
      ChainType.BITCOINSV);

  private final ConcurrentHashMap<String, EvmChainRegistration> evmChains = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, UtxoChainRegistration> utxoChains = new ConcurrentHashMap<>();

  private ChainRegistry() {
  }

  public static ChainRegistry getInstance() {
    return INSTANCE;
  }

  private static String normalize(String chainType) {
    if (chainType == null) {
      throw new TokenException("chainType must not be null");
    }
    String t = chainType.trim().toUpperCase(Locale.ROOT);
    if (t.isEmpty()) {
      throw new TokenException("chainType must not be empty");
    }
    return t;
  }

  public void registerEvm(EvmChainRegistration registration) {
    String key = registration.getChainType();
    evmChains.put(key, registration);
  }

  public void registerUtxo(UtxoChainRegistration registration) {
    String key = registration.getChainType();
    utxoChains.put(key, registration);
  }

  public EvmChainRegistration getEvmRegistration(String chainType) {
    String key = normalize(chainType);
    if (ChainType.ETHEREUM.equals(key)) {
      return new EvmChainRegistration(ChainType.ETHEREUM, 1L, BIP44Util.ETHEREUM_PATH);
    }
    return evmChains.get(key);
  }

  public UtxoChainRegistration getUtxoRegistration(String chainType) {
    return utxoChains.get(normalize(chainType));
  }

  public boolean isRegisteredEvm(String chainType) {
    String key = normalize(chainType);
    return ChainType.ETHEREUM.equals(key) || evmChains.containsKey(key);
  }

  public boolean isRegisteredUtxo(String chainType) {
    String key = normalize(chainType);
    return BITCOIN_STYLE_BUILTIN.contains(key) || utxoChains.containsKey(key);
  }

  public ChainFamily resolveFamily(String chainType) {
    String key = normalize(chainType);
    if (ChainType.ETHEREUM.equals(key) || evmChains.containsKey(key)) {
      return ChainFamily.EVM;
    }
    if (ChainType.TRON.equals(key)) {
      return ChainFamily.TRON;
    }
    if (ChainType.FILECOIN.equals(key)) {
      return ChainFamily.FILECOIN;
    }
    if (ChainType.EOS.equals(key)) {
      return ChainFamily.EOS;
    }
    if (BITCOIN_STYLE_BUILTIN.contains(key) || utxoChains.containsKey(key)) {
      return ChainFamily.BITCOIN_STYLE_UTXO;
    }
    throw new TokenException("Unsupported chain type: " + key);
  }

  public boolean isSupportedChainType(String chainType) {
    try {
      resolveFamily(chainType);
      return true;
    } catch (TokenException e) {
      return false;
    }
  }

  public long getEvmChainId(String chainType) {
    EvmChainRegistration r = getEvmRegistration(chainType);
    if (r == null) {
      throw new TokenException("Not an EVM chain type: " + chainType);
    }
    return r.getChainId();
  }

  public String getDefaultMnemonicPath(String chainType) {
    String key = normalize(chainType);
    if (ChainType.ETHEREUM.equals(key)) {
      return BIP44Util.ETHEREUM_PATH;
    }
    EvmChainRegistration ev = evmChains.get(key);
    if (ev != null) {
      return ev.getDefaultMnemonicPath();
    }
    if (ChainType.TRON.equals(key)) {
      return BIP44Util.TRON_PATH;
    }
    if (ChainType.FILECOIN.equals(key)) {
      return BIP44Util.FILECOIN_PATH;
    }
    if (ChainType.EOS.equals(key)) {
      return BIP44Util.EOS_LEDGER;
    }
    UtxoChainRegistration ut = utxoChains.get(key);
    if (ut != null) {
      return ut.getDefaultMnemonicPath();
    }
    return null;
  }
}
