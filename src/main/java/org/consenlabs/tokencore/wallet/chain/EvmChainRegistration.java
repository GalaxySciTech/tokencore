package org.consenlabs.tokencore.wallet.chain;

import com.google.common.base.Preconditions;
import org.consenlabs.tokencore.wallet.model.TokenException;

import java.util.Objects;

/**
 * Runtime registration for an EVM-compatible chain. Wallets are disambiguated by {@code chainType}
 * string (unique per chain) even when the derived address matches another EVM chain.
 */
public final class EvmChainRegistration {
  private final String chainType;
  private final long chainId;
  private final String defaultMnemonicPath;

  public EvmChainRegistration(String chainType, long chainId, String defaultMnemonicPath) {
    Preconditions.checkNotNull(chainType, "chainType");
    Preconditions.checkNotNull(defaultMnemonicPath, "defaultMnemonicPath");
    String normalized = chainType.trim().toUpperCase();
    if (normalized.isEmpty()) {
      throw new TokenException("chainType must not be empty");
    }
    if (chainId <= 0) {
      throw new TokenException("chainId must be positive");
    }
    this.chainType = normalized;
    this.chainId = chainId;
    this.defaultMnemonicPath = defaultMnemonicPath.trim();
  }

  public String getChainType() {
    return chainType;
  }

  public long getChainId() {
    return chainId;
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
    EvmChainRegistration that = (EvmChainRegistration) o;
    return chainId == that.chainId
        && chainType.equals(that.chainType)
        && defaultMnemonicPath.equals(that.defaultMnemonicPath);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chainType, chainId, defaultMnemonicPath);
  }
}
