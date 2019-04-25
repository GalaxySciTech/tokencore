package org.consenlabs.tokencore.foundation.utils;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.network.DashMainNetParams;
import org.consenlabs.tokencore.wallet.network.LitecoinMainNetParams;

/**
 * Created by pie on 2018/12/5 15: 32.
 */
public class MetaUtil {
    public static NetworkParameters getNetWork(Metadata metadata) {
        NetworkParameters network = null;
        if (metadata.getChainType() == null) return MainNetParams.get();
        switch (metadata.getChainType()) {
            case ChainType.LITECOIN:
                network = LitecoinMainNetParams.get();
                break;
            case ChainType.BITCOIN:
                network = metadata.isMainNet() ? MainNetParams.get() : TestNet3Params.get();
                break;
            case ChainType.USDT:
                network = metadata.isMainNet() ? MainNetParams.get() : TestNet3Params.get();
                break;
            case ChainType.DASH:
                network = DashMainNetParams.get();
                break;
        }
        return network;
    }
}
