package org.consenlabs.tokencore.wallet.model;

public class ChainType {
    public final static String ETHEREUM = "ETHEREUM";
    public final static String BITCOIN = "BITCOIN";
    public final static String EOS = "EOS";
    public final static String LITECOIN = "LITECOIN";
    public final static String DASH = "DASH";
    public final static String BITCOINCASH = "BITCOINCASH";
    public final static String BITCOINSV = "BITCOINSV";
    public final static String DOGECOIN = "DOGECOIN";
    public final static String TRON = "TRON";
    public final static String FILECOIN = "FILECOIN";


    public static void validate(String type) {
        if (!ETHEREUM.equals(type) &&
                !BITCOIN.equals(type) &&
                !EOS.equals(type) &&
                !LITECOIN.equals(type) &&
                !DASH.equals(type) &&
                !BITCOINSV.equals(type) &&
                !BITCOINCASH.equals(type) &&
                !DOGECOIN.equals(type) &&
                !TRON.equals(type)&&
                !FILECOIN.equals(type)) {
            throw new TokenException(Messages.WALLET_INVALID_TYPE);
        }
    }
}
