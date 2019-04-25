package org.consenlabs.tokencore.wallet.model;

public class ChainType {
  public final static String ETHEREUM = "ETHEREUM";
  public final static String BITCOIN = "BITCOIN";
  public final static String EOS = "EOS";
  public final static String LITECOIN = "LITECOIN";
  public final static String USDT = "USDT";
  public final static String DASH = "DASH";


  public static void validate(String type) {
    if (!ETHEREUM.equals(type) && !BITCOIN.equals(type) && !EOS.equals(type)&&!LITECOIN.equals(type)&&!USDT.equals(type)&&!DASH.equals(type)) {
      throw new TokenException(Messages.WALLET_INVALID_TYPE);
    }
  }
}
