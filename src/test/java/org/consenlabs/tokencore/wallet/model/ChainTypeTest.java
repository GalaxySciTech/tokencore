package org.consenlabs.tokencore.wallet.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ChainTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"ETHEREUM", "BITCOIN", "EOS", "LITECOIN", "DASH",
            "BITCOINSV", "BITCOINCASH", "DOGECOIN", "TRON", "FILECOIN"})
    void validate_shouldAcceptSupportedChains(String chainType) {
        assertDoesNotThrow(() -> ChainType.validate(chainType));
    }

    @Test
    void validate_shouldRejectUnknownChain() {
        assertThrows(TokenException.class, () -> ChainType.validate("UNKNOWN"));
    }

    @Test
    void validate_shouldRejectNull() {
        assertThrows(TokenException.class, () -> ChainType.validate(null));
    }

    @Test
    void constants_shouldHaveCorrectValues() {
        assertEquals("ETHEREUM", ChainType.ETHEREUM);
        assertEquals("BITCOIN", ChainType.BITCOIN);
        assertEquals("EOS", ChainType.EOS);
        assertEquals("LITECOIN", ChainType.LITECOIN);
        assertEquals("DASH", ChainType.DASH);
        assertEquals("BITCOINCASH", ChainType.BITCOINCASH);
        assertEquals("BITCOINSV", ChainType.BITCOINSV);
        assertEquals("DOGECOIN", ChainType.DOGECOIN);
        assertEquals("TRON", ChainType.TRON);
        assertEquals("FILECOIN", ChainType.FILECOIN);
    }
}
