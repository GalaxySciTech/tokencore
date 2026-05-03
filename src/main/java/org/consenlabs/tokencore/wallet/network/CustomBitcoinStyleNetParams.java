package org.consenlabs.tokencore.wallet.network;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 * Bitcoin-style {@link NetworkParameters} for registered UTXO chains. Difficulty/genesis template
 * is copied from mainnet or testnet3; address and BIP32 headers are supplied by the caller.
 */
public final class CustomBitcoinStyleNetParams extends AbstractBitcoinNetParams {

  private final String netId;

  /**
   * @param id unique network id (e.g. "custom.myregtest")
   * @param useMainnetDifficultyGenesis if true, clone difficulty/genesis scaffolding from mainnet; else from testnet3
   */
  public CustomBitcoinStyleNetParams(
      String id,
      int addressHeader,
      int p2shHeader,
      int dumpedPrivateKeyHeader,
      int bip32HeaderPub,
      int bip32HeaderPriv,
      boolean useMainnetDifficultyGenesis) {
    super();
    if (id == null || id.trim().isEmpty()) {
      throw new IllegalArgumentException("id must not be empty");
    }
    this.netId = id.trim();
    NetworkParameters template = useMainnetDifficultyGenesis ? MainNetParams.get() : TestNet3Params.get();
    this.genesisBlock = template.getGenesisBlock();
    this.maxTarget = template.getMaxTarget();
    this.port = template.getPort();
    this.packetMagic = template.getPacketMagic();
    this.interval = template.getInterval();
    this.targetTimespan = template.getTargetTimespan();
    this.majorityEnforceBlockUpgrade = template.getMajorityEnforceBlockUpgrade();
    this.majorityRejectBlockOutdated = template.getMajorityRejectBlockOutdated();
    this.majorityWindow = template.getMajorityWindow();
    this.subsidyDecreaseBlockCount = template.getSubsidyDecreaseBlockCount();
    this.spendableCoinbaseDepth = template.getSpendableCoinbaseDepth();
    this.checkpoints.clear();

    this.addressHeader = addressHeader;
    this.p2shHeader = p2shHeader;
    this.dumpedPrivateKeyHeader = dumpedPrivateKeyHeader;
    this.acceptableAddressCodes = new int[] {this.addressHeader, this.p2shHeader};
    this.bip32HeaderPub = bip32HeaderPub;
    this.bip32HeaderPriv = bip32HeaderPriv;
    this.id = this.netId;
  }

  @Override
  public String getPaymentProtocolId() {
    return "main";
  }
}
