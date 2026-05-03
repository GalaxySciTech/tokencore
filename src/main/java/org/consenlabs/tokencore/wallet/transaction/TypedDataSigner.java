package org.consenlabs.tokencore.wallet.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import org.bitcoinj.core.ECKey;

/**
 * Sign EIP-712 typed data (v4) using the same secp256k1 recovery format as {@link EthereumSign}.
 */
public final class TypedDataSigner {

  private TypedDataSigner() {
  }

  public static SignatureData signTypedDataV4(JsonNode typedDataRoot, byte[] privateKeyBytes) {
    byte[] digest = Eip712Hasher.hashTypedDataV4(typedDataRoot);
    return EthereumSign.signAsRecoverable(digest, ECKey.fromPrivate(privateKeyBytes));
  }

  public static String signTypedDataV4Hex(JsonNode typedDataRoot, byte[] privateKeyBytes) {
    return signTypedDataV4(typedDataRoot, privateKeyBytes).toString();
  }
}
