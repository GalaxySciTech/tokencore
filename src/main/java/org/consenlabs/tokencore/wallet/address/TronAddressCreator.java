package org.consenlabs.tokencore.wallet.address;

import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.consenlabs.tokencore.foundation.crypto.Hash;
import org.consenlabs.tokencore.foundation.utils.NumericUtil;

import java.math.BigInteger;
import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

/**
 * Created by pie on 2020/9/6 22: 28.
 */
public class TronAddressCreator implements AddressCreator {

    private static final byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;

    private static final int PUBLIC_KEY_SIZE = 64;

    private String encode58Check(byte[] input) {
        byte[] hash0 = Hash.sha256(input);
        byte[] hash1 = Hash.sha256(hash0);
        byte[] inputCheck = new byte[input.length + 4];
        System.arraycopy(input, 0, inputCheck, 0, input.length);
        System.arraycopy(hash1, 0, inputCheck, input.length, 4);
        return Base58.encode(inputCheck);
    }

    public String fromPublicKey(BigInteger publicKey) {
        byte[] pubKeyBytes = NumericUtil.bigIntegerToBytesWithZeroPadded(publicKey, PUBLIC_KEY_SIZE);
        return publicKeyToAddress(pubKeyBytes);
    }

    private String publicKeyToAddress(byte[] pubKeyBytes) {
        byte[] hashedBytes = Hash.keccak256(pubKeyBytes);
        byte[] address = copyOfRange(hashedBytes, 11, hashedBytes.length);
        address[0] = ADD_PRE_FIX_BYTE_MAINNET;
        return encode58Check(address);
    }

    @Override
    public String fromPrivateKey(String prvKeyHex) {
        ECKey key = ECKey.fromPrivate(NumericUtil.hexToBytes(prvKeyHex), false);
        return fromECKey(key);
    }

    @Override
    public String fromPrivateKey(byte[] prvKeyBytes) {
        ECKey key = ECKey.fromPrivate(prvKeyBytes, false);
        return fromECKey(key);
    }

    private String fromECKey(ECKey key) {
        byte[] pubKeyBytes = key.getPubKey();
        return publicKeyToAddress(Arrays.copyOfRange(pubKeyBytes, 1, pubKeyBytes.length));
    }

}
