package org.consenlabs.tokencore.foundation.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class NumericUtilTest {

    @Test
    void generateRandomBytes_shouldReturnCorrectLength() {
        byte[] bytes16 = NumericUtil.generateRandomBytes(16);
        assertEquals(16, bytes16.length);

        byte[] bytes32 = NumericUtil.generateRandomBytes(32);
        assertEquals(32, bytes32.length);
    }

    @Test
    void generateRandomBytes_shouldReturnDifferentValues() {
        byte[] a = NumericUtil.generateRandomBytes(32);
        byte[] b = NumericUtil.generateRandomBytes(32);
        assertNotEquals(NumericUtil.bytesToHex(a), NumericUtil.bytesToHex(b));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0xabcdef", "0XABCDEF", "abcdef", "1234", "0x00ff"})
    void isValidHex_shouldReturnTrueForValidHex(String hex) {
        assertTrue(NumericUtil.isValidHex(hex));
    }

    @ParameterizedTest
    @NullSource
    void isValidHex_shouldReturnFalseForNull(String hex) {
        assertFalse(NumericUtil.isValidHex(hex));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0x", "xyz", "0xgg"})
    void isValidHex_shouldReturnFalseForInvalidHex(String hex) {
        assertFalse(NumericUtil.isValidHex(hex));
    }

    @Test
    void cleanHexPrefix_shouldRemovePrefix() {
        assertEquals("abcdef", NumericUtil.cleanHexPrefix("0xabcdef"));
        assertEquals("abcdef", NumericUtil.cleanHexPrefix("abcdef"));
    }

    @Test
    void prependHexPrefix_shouldAddPrefix() {
        assertEquals("0xabcdef", NumericUtil.prependHexPrefix("abcdef"));
        assertEquals("0xabcdef", NumericUtil.prependHexPrefix("0xabcdef"));
    }

    @Test
    void hexToBytes_shouldConvertCorrectly() {
        byte[] bytes = NumericUtil.hexToBytes("0x0102ff");
        assertEquals(3, bytes.length);
        assertEquals(1, bytes[0]);
        assertEquals(2, bytes[1]);
        assertEquals((byte) 0xff, bytes[2]);
    }

    @Test
    void hexToBytes_emptyInput() {
        byte[] bytes = NumericUtil.hexToBytes("");
        assertEquals(0, bytes.length);
    }

    @Test
    void bytesToHex_shouldConvertCorrectly() {
        byte[] bytes = {0x01, 0x02, (byte) 0xff};
        assertEquals("0102ff", NumericUtil.bytesToHex(bytes));
    }

    @Test
    void bytesToHex_emptyArray() {
        assertEquals("", NumericUtil.bytesToHex(new byte[0]));
    }

    @Test
    void roundTrip_hexConversion() {
        String original = "deadbeef01020304";
        String result = NumericUtil.bytesToHex(NumericUtil.hexToBytes(original));
        assertEquals(original, result);
    }

    @Test
    void hexToBigInteger_shouldConvert() {
        BigInteger result = NumericUtil.hexToBigInteger("0xff");
        assertEquals(BigInteger.valueOf(255), result);
    }

    @Test
    void bigIntegerToHex_shouldConvert() {
        String hex = NumericUtil.bigIntegerToHex(BigInteger.valueOf(255));
        assertEquals("ff", hex);
    }

    @Test
    void bigIntegerToBytesWithZeroPadded_correctPadding() {
        byte[] result = NumericUtil.bigIntegerToBytesWithZeroPadded(BigInteger.ONE, 32);
        assertEquals(32, result.length);
        assertEquals(1, result[31]);
        for (int i = 0; i < 31; i++) {
            assertEquals(0, result[i]);
        }
    }

    @Test
    void bigIntegerToBytesWithZeroPadded_throwsOnTooLargeInput() {
        BigInteger tooLarge = BigInteger.ONE.shiftLeft(256);
        assertThrows(RuntimeException.class,
                () -> NumericUtil.bigIntegerToBytesWithZeroPadded(tooLarge, 32));
    }

    @Test
    void intToBytes_andBack() {
        byte[] bytes = NumericUtil.intToBytes(256);
        int result = NumericUtil.bytesToInt(new byte[]{0, 0, bytes[0], bytes[1]});
        assertEquals(256, result);
    }

    @Test
    void reverseBytes_shouldReverse() {
        byte[] input = {1, 2, 3, 4};
        byte[] reversed = NumericUtil.reverseBytes(input);
        assertArrayEquals(new byte[]{4, 3, 2, 1}, reversed);
    }
}
