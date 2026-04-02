package org.consenlabs.tokencore.wallet.transaction;

import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class EthereumTransactionTest {

    @Test
    void encodeToRLP_shouldProduceNonEmptyBytes() {
        EthereumTransaction tx = new EthereumTransaction(
                BigInteger.ZERO,
                BigInteger.valueOf(20_000_000_000L),
                BigInteger.valueOf(21000),
                "0x7e5f4552091a69125d5dfcb7b8c2659029395bdf",
                BigInteger.valueOf(1_000_000_000_000_000_000L),
                ""
        );

        SignatureData emptySignature = new SignatureData(1, new byte[]{}, new byte[]{});
        byte[] encoded = tx.encodeToRLP(emptySignature);
        assertNotNull(encoded);
        assertTrue(encoded.length > 0);
    }

    @Test
    void signTransaction_shouldProduceValidSignedTx() {
        EthereumTransaction tx = new EthereumTransaction(
                BigInteger.valueOf(9),
                BigInteger.valueOf(20_000_000_000L),
                BigInteger.valueOf(21000),
                "0x3535353535353535353535353535353535353535",
                BigInteger.valueOf(1_000_000_000_000_000_000L),
                ""
        );

        byte[] privateKey = NumericUtil.hexToBytes(
                "4646464646464646464646464646464646464646464646464646464646464646");

        String signedTx = tx.signTransaction(1, privateKey);
        assertNotNull(signedTx);
        assertTrue(signedTx.length() > 0);
        assertTrue(NumericUtil.isValidHex(signedTx));
    }

    @Test
    void calcTxHash_shouldReturn66CharHex() {
        EthereumTransaction tx = new EthereumTransaction(
                BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE, "", BigInteger.ZERO, "");

        String signedTx = "f86c09850" + "4a817c800" + "82520894" +
                "3535353535353535353535353535353535353535" +
                "880de0b6b3a7640000" + "80" +
                "25a028ef61340bd939bc2195fe537567866003e1a15d3c71ff63e1590620aa636276" +
                "a067cbe9d8997f761aecb703304b3800ccf555c9f3dc64214b297fb1966a3b6d83";
        String hash = tx.calcTxHash(signedTx);
        assertNotNull(hash);
        assertTrue(hash.startsWith("0x"));
    }

    @Test
    void getters_shouldReturnConstructorValues() {
        EthereumTransaction tx = new EthereumTransaction(
                BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(21000),
                "0xabc", BigInteger.ZERO, "0xdeadbeef");

        assertEquals(BigInteger.ONE, tx.getNonce());
        assertEquals(BigInteger.TEN, tx.getGasPrice());
        assertEquals(BigInteger.valueOf(21000), tx.getGasLimit());
        assertEquals("0xabc", tx.getTo());
        assertEquals(BigInteger.ZERO, tx.getValue());
        assertEquals("deadbeef", tx.getData());
    }
}
