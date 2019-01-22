package org.consenlabs.tokencore.wallet.address;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.Messages;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.TokenException;
import org.consenlabs.tokencore.wallet.network.LitecoinMainNetParams;

public class AddressCreatorManager {

  public static AddressCreator getInstance(String type, boolean isMainnet, String segWit) {
    if (ChainType.ETHEREUM.equals(type)) {
      return new EthereumAddressCreator();
    }else if(ChainType.LITECOIN.equals(type)){
      NetworkParameters network= LitecoinMainNetParams.get();
      return new BitcoinAddressCreator(network);
    } else if (ChainType.BITCOIN.equals(type)) {

      NetworkParameters network = isMainnet ? MainNetParams.get() : TestNet3Params.get();
      if (Metadata.P2WPKH.equals(segWit)) {
        return new SegWitBitcoinAddressCreator(network);
      }
      return new BitcoinAddressCreator(network);
    } else {
      throw new TokenException(Messages.WALLET_INVALID_TYPE);
    }
  }

}
