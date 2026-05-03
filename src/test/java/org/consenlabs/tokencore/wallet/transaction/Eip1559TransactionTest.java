package org.consenlabs.tokencore.wallet.transaction;

import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Eip1559TransactionTest {

  @Test
  void sign_producesType2PayloadAndTxHash() {
    Eip1559Transaction tx =
        new Eip1559Transaction(
            BigInteger.valueOf(1),
            BigInteger.ZERO,
            BigInteger.valueOf(2_000_000_000L),
            BigInteger.valueOf(100_000_000_000L),
            BigInteger.valueOf(21_000),
            "0x3535353535353535353535353535353535353535",
            BigInteger.valueOf(1_000_000_000_000_000_000L),
            "");

    byte[] pk =
        NumericUtil.hexToBytes(
            "0xc85ef7d79691fe79591b22373951e8f881976134b5f761f16494d6877a6dd51a");
    String signed = tx.sign(1, pk);
    assertTrue(signed.startsWith("02"), "raw tx must be type-2 (0x02 prefix when hex)");
    assertTrue(signed.length() > 100);
    String hash =
        NumericUtil.prependHexPrefix(
            org.consenlabs.tokencore.foundation.crypto.Hash.keccak256(signed));
    assertEquals(66, hash.length());
  }
}
