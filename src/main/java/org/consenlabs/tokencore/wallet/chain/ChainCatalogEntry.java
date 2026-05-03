package org.consenlabs.tokencore.wallet.chain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON row for {@link ChainCatalogLoader}. Extra fields are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainCatalogEntry {

  private String chainType;

  /**
   * EVM, UTXO (bitcoin-style), or built-in TRON, FILECOIN, EOS (no-op registration for catalog consistency).
   */
  private String family;

  private Long chainId;

  private Integer slip44;

  private String defaultMnemonicPath;

  private Integer addressHeader;
  private Integer p2shHeader;
  private Integer dumpedPrivateKeyHeader;
  private Integer bip32HeaderPub;
  private Integer bip32HeaderPriv;
  private Boolean useMainnetDifficultyGenesis;

  public String getChainType() {
    return chainType;
  }

  public void setChainType(String chainType) {
    this.chainType = chainType;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public Long getChainId() {
    return chainId;
  }

  public void setChainId(Long chainId) {
    this.chainId = chainId;
  }

  public Integer getSlip44() {
    return slip44;
  }

  public void setSlip44(Integer slip44) {
    this.slip44 = slip44;
  }

  public String getDefaultMnemonicPath() {
    return defaultMnemonicPath;
  }

  public void setDefaultMnemonicPath(String defaultMnemonicPath) {
    this.defaultMnemonicPath = defaultMnemonicPath;
  }

  public Integer getAddressHeader() {
    return addressHeader;
  }

  public void setAddressHeader(Integer addressHeader) {
    this.addressHeader = addressHeader;
  }

  public Integer getP2shHeader() {
    return p2shHeader;
  }

  public void setP2shHeader(Integer p2shHeader) {
    this.p2shHeader = p2shHeader;
  }

  public Integer getDumpedPrivateKeyHeader() {
    return dumpedPrivateKeyHeader;
  }

  public void setDumpedPrivateKeyHeader(Integer dumpedPrivateKeyHeader) {
    this.dumpedPrivateKeyHeader = dumpedPrivateKeyHeader;
  }

  public Integer getBip32HeaderPub() {
    return bip32HeaderPub;
  }

  public void setBip32HeaderPub(Integer bip32HeaderPub) {
    this.bip32HeaderPub = bip32HeaderPub;
  }

  public Integer getBip32HeaderPriv() {
    return bip32HeaderPriv;
  }

  public void setBip32HeaderPriv(Integer bip32HeaderPriv) {
    this.bip32HeaderPriv = bip32HeaderPriv;
  }

  public Boolean getUseMainnetDifficultyGenesis() {
    return useMainnetDifficultyGenesis;
  }

  public void setUseMainnetDifficultyGenesis(Boolean useMainnetDifficultyGenesis) {
    this.useMainnetDifficultyGenesis = useMainnetDifficultyGenesis;
  }
}
