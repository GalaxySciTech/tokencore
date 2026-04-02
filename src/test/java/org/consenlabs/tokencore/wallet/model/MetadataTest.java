package org.consenlabs.tokencore.wallet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetadataTest {

    @Test
    void defaultConstructor_shouldCreateEmptyMetadata() {
        Metadata metadata = new Metadata();
        assertNull(metadata.getName());
        assertNull(metadata.getChainType());
        assertNull(metadata.getNetwork());
        assertEquals("NORMAL", metadata.getMode());
    }

    @Test
    void paramConstructor_shouldSetFields() {
        Metadata metadata = new Metadata(ChainType.ETHEREUM, Network.MAINNET, "Test", "hint");
        assertEquals(ChainType.ETHEREUM, metadata.getChainType());
        assertEquals(Network.MAINNET, metadata.getNetwork());
        assertEquals("Test", metadata.getName());
        assertEquals("hint", metadata.getPasswordHint());
        assertTrue(metadata.getTimestamp() > 0);
    }

    @Test
    void isMainNet_shouldReturnTrue() {
        Metadata metadata = new Metadata();
        metadata.setNetwork(Network.MAINNET);
        assertTrue(metadata.isMainNet());
    }

    @Test
    void isMainNet_shouldReturnFalse() {
        Metadata metadata = new Metadata();
        metadata.setNetwork(Network.TESTNET);
        assertFalse(metadata.isMainNet());
    }

    @Test
    void clone_shouldCreateIndependentCopy() {
        Metadata original = new Metadata();
        original.setChainType(ChainType.BITCOIN);
        original.setName("Original");
        original.setSegWit(Metadata.P2WPKH);

        Metadata cloned = original.clone();
        assertEquals(original.getChainType(), cloned.getChainType());
        assertEquals(original.getName(), cloned.getName());

        cloned.setName("Cloned");
        assertNotEquals(original.getName(), cloned.getName());
    }

    @Test
    void segWitConstants() {
        assertEquals("P2WPKH", Metadata.P2WPKH);
        assertEquals("NONE", Metadata.NONE);
    }

    @Test
    void walletTypeConstants() {
        assertEquals("HD", Metadata.HD);
        assertEquals("V3", Metadata.V3);
    }
}
