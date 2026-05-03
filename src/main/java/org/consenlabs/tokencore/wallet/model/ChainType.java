package org.consenlabs.tokencore.wallet.model;

import org.consenlabs.tokencore.wallet.chain.ChainRegistry;

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
        if (!ChainRegistry.getInstance().isSupportedChainType(type)) {
            throw new TokenException(Messages.WALLET_INVALID_TYPE);
        }
    }
}
