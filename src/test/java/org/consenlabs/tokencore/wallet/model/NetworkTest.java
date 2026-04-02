package org.consenlabs.tokencore.wallet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetworkTest {

    @Test
    void isMainnet_shouldReturnTrue() {
        Network network = new Network(Network.MAINNET);
        assertTrue(network.isMainnet());
    }

    @Test
    void isMainnet_shouldReturnFalse() {
        Network network = new Network(Network.TESTNET);
        assertFalse(network.isMainnet());
    }

    @Test
    void isMainnet_shouldBeCaseInsensitive() {
        Network network = new Network("mainnet");
        assertTrue(network.isMainnet());
    }

    @Test
    void constants_shouldBeCorrect() {
        assertEquals("MAINNET", Network.MAINNET);
        assertEquals("TESTNET", Network.TESTNET);
        assertEquals("KOVAN", Network.KOVAN);
        assertEquals("ROPSTEN", Network.ROPSTEN);
    }
}
