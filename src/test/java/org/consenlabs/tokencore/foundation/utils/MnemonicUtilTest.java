package org.consenlabs.tokencore.foundation.utils;

import org.consenlabs.tokencore.wallet.model.TokenException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MnemonicUtilTest {

    @Test
    void randomMnemonicCodes_shouldGenerate12Words() {
        List<String> codes = MnemonicUtil.randomMnemonicCodes();
        assertEquals(12, codes.size());
        for (String word : codes) {
            assertNotNull(word);
            assertFalse(word.isEmpty());
        }
    }

    @Test
    void randomMnemonicCodes_shouldBeDifferentEachTime() {
        List<String> a = MnemonicUtil.randomMnemonicCodes();
        List<String> b = MnemonicUtil.randomMnemonicCodes();
        assertNotEquals(a, b);
    }

    @Test
    void randomMnemonicStr_shouldReturnSpaceSeparatedWords() {
        String mnemonic = MnemonicUtil.randomMnemonicStr();
        assertNotNull(mnemonic);
        String[] words = mnemonic.split(" ");
        assertEquals(12, words.length);
    }

    @Test
    void validateMnemonics_shouldAcceptValid() {
        List<String> codes = MnemonicUtil.randomMnemonicCodes();
        assertDoesNotThrow(() -> MnemonicUtil.validateMnemonics(codes));
    }

    @Test
    void validateMnemonics_shouldRejectInvalidLength() {
        List<String> tooShort = Arrays.asList("abandon", "abandon", "abandon");
        assertThrows(TokenException.class, () -> MnemonicUtil.validateMnemonics(tooShort));
    }

    @Test
    void validateMnemonics_shouldRejectInvalidWords() {
        List<String> invalid = Arrays.asList(
                "notaword", "notaword", "notaword", "notaword",
                "notaword", "notaword", "notaword", "notaword",
                "notaword", "notaword", "notaword", "notaword"
        );
        assertThrows(TokenException.class, () -> MnemonicUtil.validateMnemonics(invalid));
    }

    @Test
    void toMnemonicCodes_string_shouldTrimAndNormalizeWhitespace() {
        String raw = "  abandon   abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about  ";
        List<String> codes = MnemonicUtil.toMnemonicCodes(raw);
        assertEquals(12, codes.size());
        assertEquals("abandon", codes.get(0));
        assertEquals("about", codes.get(11));
    }

    @Test
    void toMnemonicCodes_string_shouldRejectBlankInput() {
        assertThrows(TokenException.class, () -> MnemonicUtil.toMnemonicCodes("   "));
    }

    @Test
    void toMnemonicCodes_shouldConvertEntropy() {
        byte[] entropy = new byte[16];
        List<String> codes = MnemonicUtil.toMnemonicCodes(entropy);
        assertEquals(12, codes.size());
        MnemonicUtil.validateMnemonics(codes);
    }
}
