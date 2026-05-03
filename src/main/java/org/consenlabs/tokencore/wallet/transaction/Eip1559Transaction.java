package org.consenlabs.tokencore.wallet.transaction;

import org.bitcoinj.core.ECKey;
import org.consenlabs.tokencore.foundation.crypto.Hash;
import org.consenlabs.tokencore.foundation.rlp.RlpEncoder;
import org.consenlabs.tokencore.foundation.rlp.RlpList;
import org.consenlabs.tokencore.foundation.rlp.RlpString;
import org.consenlabs.tokencore.foundation.rlp.RlpType;
import org.consenlabs.tokencore.foundation.utils.ByteUtil;
import org.consenlabs.tokencore.foundation.utils.NumericUtil;
import org.consenlabs.tokencore.wallet.Wallet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EIP-1559 (type-2) transaction signing. Raw format: {@code 0x02 || rlp([...])}.
 */
public class Eip1559Transaction implements TransactionSigner {

  private static final int TYPE_2 = 0x02;

  private final BigInteger chainId;
  private final BigInteger nonce;
  private final BigInteger maxPriorityFeePerGas;
  private final BigInteger maxFeePerGas;
  private final BigInteger gasLimit;
  private final String to;
  private final BigInteger value;
  private final String data;

  public Eip1559Transaction(
      BigInteger chainId,
      BigInteger nonce,
      BigInteger maxPriorityFeePerGas,
      BigInteger maxFeePerGas,
      BigInteger gasLimit,
      String to,
      BigInteger value,
      String data) {
    this.chainId = chainId;
    this.nonce = nonce;
    this.maxPriorityFeePerGas = maxPriorityFeePerGas;
    this.maxFeePerGas = maxFeePerGas;
    this.gasLimit = gasLimit;
    this.to = to;
    this.value = value;
    if (data != null) {
      this.data = NumericUtil.cleanHexPrefix(data);
    } else {
      this.data = "";
    }
  }

  @Override
  public TxSignResult signTransaction(String chainID, String password, Wallet wallet) {
    if (chainId.signum() <= 0 || chainId.bitLength() > 31) {
      throw new IllegalArgumentException("chainId must be a positive 32-bit integer");
    }
    int expected = chainId.intValue();
    int provided = Integer.parseInt(chainID);
    if (expected != provided) {
      throw new IllegalArgumentException("chainID argument must match transaction chainId");
    }
    String signed = sign(expected, wallet.decryptMainKey(password));
    String txHash = NumericUtil.prependHexPrefix(Hash.keccak256(signed));
    return new TxSignResult(signed, txHash);
  }

  public String sign(int chainIdInt, byte[] privateKey) {
    byte[] unsigned = encodeUnsigned();
    byte[] hash = Hash.keccak256(ByteUtil.concat(new byte[] {(byte) TYPE_2}, unsigned));
    SignatureData base = EthereumSign.signAsRecoverable(hash, ECKey.fromPrivate(privateKey));
    int yParity = (base.getV() - 27) & 1;
    List<RlpType> all = new ArrayList<>(unsignedList());
    all.add(RlpString.create(yParity));
    all.add(RlpString.create(ByteUtil.trimLeadingZeroes(base.getR())));
    all.add(RlpString.create(ByteUtil.trimLeadingZeroes(base.getS())));
    byte[] body = RlpEncoder.encode(new RlpList(all));
    return NumericUtil.bytesToHex(ByteUtil.concat(new byte[] {(byte) TYPE_2}, body));
  }

  private List<RlpType> unsignedList() {
    List<RlpType> result = new ArrayList<>();
    result.add(RlpString.create(chainId));
    result.add(RlpString.create(nonce));
    result.add(RlpString.create(maxPriorityFeePerGas));
    result.add(RlpString.create(maxFeePerGas));
    result.add(RlpString.create(gasLimit));
    if (to != null && to.length() > 0) {
      result.add(RlpString.create(NumericUtil.hexToBytes(to)));
    } else {
      result.add(RlpString.create(""));
    }
    result.add(RlpString.create(value));
    result.add(RlpString.create(NumericUtil.hexToBytes(data)));
    result.add(new RlpList(Collections.<RlpType>emptyList()));
    return result;
  }

  private byte[] encodeUnsigned() {
    return RlpEncoder.encode(new RlpList(unsignedList()));
  }
}
