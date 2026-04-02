package org.consenlabs.tokencore.wallet.address;

import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EthereumAddressCreatorTest {

    private final EthereumAddressCreator creator = new EthereumAddressCreator();

    @Test
    void fromPrivateKey_shouldDeriveValidAddress() {
        String address = creator.fromPrivateKey(
                "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c");
        assertNotNull(address);
        assertEquals(40, address.length());
        assertTrue(NumericUtil.isValidHex(address));
    }

    @Test
    void fromPrivateKey_bytes_shouldWork() {
        byte[] privKey = NumericUtil.hexToBytes(
                "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c");
        String address = creator.fromPrivateKey(privKey);
        assertNotNull(address);
        assertEquals(40, address.length());
    }

    @Test
    void samePrivateKey_shouldProduceSameAddress() {
        String key = "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c";
        String addr1 = creator.fromPrivateKey(key);
        String addr2 = creator.fromPrivateKey(NumericUtil.hexToBytes(key));
        assertEquals(addr1, addr2);
    }
}
