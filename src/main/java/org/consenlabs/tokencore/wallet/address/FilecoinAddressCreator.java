package org.consenlabs.tokencore.wallet.address;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.util.HexUtil;
import com.filecoinj.crypto.ECKey;
import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import ove.crypto.digest.Blake2b;

import java.util.Arrays;

public class FilecoinAddressCreator  implements AddressCreator{

    private String publicKeyToAddress(byte[] pubKeyBytes) {
        Blake2b.Digest digest = Blake2b.Digest.newInstance(20);
        String hash = HexUtil.encodeHexStr(digest.digest(pubKeyBytes));

        String pubKeyHash = "01" + HexUtil.encodeHexStr(digest.digest(pubKeyBytes));

        Blake2b.Digest blake2b3 = Blake2b.Digest.newInstance(4);
        String checksum = HexUtil.encodeHexStr(blake2b3.digest(HexUtil.decodeHex(pubKeyHash)));

        return "f1" + Base32.encode(HexUtil.decodeHex(hash + checksum)).toLowerCase();
    }

    private String fromECKey(ECKey key) {
        byte[] pubKeyBytes = key.getPubKey();
        return publicKeyToAddress(Arrays.copyOfRange(pubKeyBytes, 1, pubKeyBytes.length));
    }

    @Override
    public String fromPrivateKey(String prvKeyHex) {
        ECKey key = ECKey.fromPrivate(NumericUtil.hexToBytes(prvKeyHex));
        return fromECKey(key);
    }

    @Override
    public String fromPrivateKey(byte[] prvKeyBytes) {
        ECKey key = ECKey.fromPrivate(prvKeyBytes);
        return fromECKey(key);
    }
}
