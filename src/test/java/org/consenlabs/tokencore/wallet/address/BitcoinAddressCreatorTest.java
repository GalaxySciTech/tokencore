package org.consenlabs.tokencore.wallet.address;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitcoinAddressCreatorTest {

    @Test
    void fromPrivateKey_mainnet_shouldDeriveAddress() {
        BitcoinAddressCreator creator = new BitcoinAddressCreator(MainNetParams.get());
        String hexKey = "e8f32e723decf4051aefac8e2c93c9c5b214313817cdb01a1494b917c8436b35";
        String address = creator.fromPrivateKey(hexKey);
        assertNotNull(address);
        assertTrue(address.startsWith("1"));
    }

    @Test
    void fromPrivateKey_testnet_shouldDeriveAddress() {
        BitcoinAddressCreator creator = new BitcoinAddressCreator(TestNet3Params.get());
        String hexKey = "e8f32e723decf4051aefac8e2c93c9c5b214313817cdb01a1494b917c8436b35";
        String address = creator.fromPrivateKey(hexKey);
        assertNotNull(address);
        assertTrue(address.startsWith("m") || address.startsWith("n"));
    }

    @Test
    void fromPrivateKey_bytes_shouldWork() {
        BitcoinAddressCreator creator = new BitcoinAddressCreator(MainNetParams.get());
        byte[] key = NumericUtil.hexToBytes("e8f32e723decf4051aefac8e2c93c9c5b214313817cdb01a1494b917c8436b35");
        String address = creator.fromPrivateKey(key);
        assertNotNull(address);
        assertTrue(address.length() > 0);
    }
}
