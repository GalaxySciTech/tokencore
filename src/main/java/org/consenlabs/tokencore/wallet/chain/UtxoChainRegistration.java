package org.consenlabs.tokencore.wallet.chain;

import com.google.common.base.Preconditions;
import org.bitcoinj.core.NetworkParameters;
import org.consenlabs.tokencore.wallet.model.TokenException;

import java.util.Objects;

/**
 * Runtime registration for a Bitcoin-style UTXO chain using a {@link NetworkParameters} instance.
 */
public final class UtxoChainRegistration {
  private final String chainType;
  private final NetworkParameters networkParameters;
  private final String defaultMnemonicPath;

  public UtxoChainRegistration(String chainType, NetworkParameters networkParameters, String defaultMnemonicPath) {
    Preconditions.checkNotNull(chainType, "chainType");
    Preconditions.checkNotNull(networkParameters, "networkParameters");
    Preconditions.checkNotNull(defaultMnemonicPath, "defaultMnemonicPath");
    String normalized = chainType.trim().toUpperCase();
    if (normalized.isEmpty()) {
      throw new TokenException("chainType must not be empty");
    }
    this.chainType = normalized;
    this.networkParameters = networkParameters;
    this.defaultMnemonicPath = defaultMnemonicPath.trim();
  }

  public String getChainType() {
    return chainType;
  }

  public NetworkParameters getNetworkParameters() {
    return networkParameters;
  }

  public String getDefaultMnemonicPath() {
    return defaultMnemonicPath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UtxoChainRegistration that = (UtxoChainRegistration) o;
    return chainType.equals(that.chainType)
        && defaultMnemonicPath.equals(that.defaultMnemonicPath)
        && networkParameters.getId().equals(that.networkParameters.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(chainType, networkParameters.getId(), defaultMnemonicPath);
  }
}
