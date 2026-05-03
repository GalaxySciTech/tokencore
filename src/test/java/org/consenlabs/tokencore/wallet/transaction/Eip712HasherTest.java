package org.consenlabs.tokencore.wallet.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Eip712HasherTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void hashTypedDataV4_isDeterministic() throws Exception {
    String json =
        "{"
            + "\"types\":{"
            + "\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"}],"
            + "\"Message\":[{\"name\":\"text\",\"type\":\"string\"}]"
            + "},"
            + "\"primaryType\":\"Message\","
            + "\"domain\":{\"name\":\"TestDomain\"},"
            + "\"message\":{\"text\":\"hello\"}"
            + "}";
    byte[] h1 = Eip712Hasher.hashTypedDataV4(MAPPER.readTree(json));
    byte[] h2 = Eip712Hasher.hashTypedDataV4(MAPPER.readTree(json));
    assertArrayEquals(h1, h2);
    assertEquals(32, h1.length);
  }

  @Test
  void signTypedDataV4_producesRecoverableSignature() throws Exception {
    String json =
        "{"
            + "\"types\":{"
            + "\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"}],"
            + "\"Message\":[{\"name\":\"text\",\"type\":\"string\"}]"
            + "},"
            + "\"primaryType\":\"Message\","
            + "\"domain\":{\"name\":\"TestDomain\"},"
            + "\"message\":{\"text\":\"hello\"}"
            + "}";
    byte[] pk =
        org.consenlabs.tokencore.foundation.utils.NumericUtil.hexToBytes(
            "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c");
    SignatureData sig =
        TypedDataSigner.signTypedDataV4(MAPPER.readTree(json), pk);
    assertEquals(32, sig.getR().length);
    assertEquals(32, sig.getS().length);
    int v = sig.getV() & 0xff;
    assertEquals(true, v == 27 || v == 28);
  }
}
