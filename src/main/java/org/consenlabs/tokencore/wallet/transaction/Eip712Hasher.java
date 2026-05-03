package org.consenlabs.tokencore.wallet.transaction;

import com.fasterxml.jackson.databind.JsonNode;
import org.consenlabs.tokencore.foundation.crypto.Hash;
import org.consenlabs.tokencore.foundation.utils.NumericUtil;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EIP-712 typed structured data hashing (eth_signTypedData_v4).
 */
public final class Eip712Hasher {

  private Eip712Hasher() {
  }

  public static byte[] hashTypedDataV4(JsonNode root) {
    JsonNode types = root.get("types");
    String primaryType = root.get("primaryType").asText();
    JsonNode domain = root.get("domain");
    JsonNode message = root.get("message");
    Map<String, List<Field>> typeMap = parseTypes(types);
    byte[] domainSeparator = hashStruct(typeMap, "EIP712Domain", domain);
    byte[] messageHash = hashStruct(typeMap, primaryType, message);
    byte[] prefix = new byte[] {0x19, 0x01};
    return Hash.keccak256(concat(prefix, domainSeparator, messageHash));
  }

  private static Map<String, List<Field>> parseTypes(JsonNode types) {
    java.util.LinkedHashMap<String, List<Field>> map = new java.util.LinkedHashMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = types.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> e = fields.next();
      String typeName = e.getKey();
      List<Field> list = new ArrayList<>();
      for (JsonNode f : e.getValue()) {
        list.add(new Field(f.get("name").asText(), f.get("type").asText()));
      }
      map.put(typeName, list);
    }
    return map;
  }

  private static byte[] hashStruct(Map<String, List<Field>> types, String type, JsonNode data) {
    return Hash.keccak256(concat(typeHash(types, type), encodeData(types, type, data)));
  }

  private static byte[] typeHash(Map<String, List<Field>> types, String primaryType) {
    List<String> deps = new ArrayList<>(collectStructTypes(types, primaryType));
    Collections.sort(deps);
    StringBuilder enc = new StringBuilder();
    for (String t : deps) {
      enc.append(structDef(types, t));
    }
    return Hash.keccak256(enc.toString().getBytes(StandardCharsets.UTF_8));
  }

  private static Set<String> collectStructTypes(Map<String, List<Field>> types, String root) {
    Set<String> order = new LinkedHashSet<>();
    collectStructTypesDfs(types, root, order);
    return order;
  }

  private static void collectStructTypesDfs(Map<String, List<Field>> types, String typeName, Set<String> order) {
    if (!types.containsKey(typeName)) {
      return;
    }
    for (Field f : types.get(typeName)) {
      String struct = firstStructType(types, f.type);
      if (struct != null) {
        collectStructTypesDfs(types, struct, order);
      }
    }
    order.add(typeName);
  }

  /** First struct type name referenced by field type (e.g. Person from Person[]). */
  private static String firstStructType(Map<String, List<Field>> types, String fieldType) {
    String t = fieldType.trim();
    int bracket = t.indexOf('[');
    String base = bracket > 0 ? t.substring(0, bracket) : t;
    return types.containsKey(base) ? base : null;
  }

  private static String structDef(Map<String, List<Field>> types, String typeName) {
    List<Field> fields = types.get(typeName);
    if (fields == null) {
      throw new IllegalArgumentException("Unknown type: " + typeName);
    }
    StringBuilder sb = new StringBuilder();
    sb.append(typeName).append('(');
    for (int i = 0; i < fields.size(); i++) {
      if (i > 0) {
        sb.append(',');
      }
      sb.append(fields.get(i).type).append(' ').append(fields.get(i).name);
    }
    sb.append(')');
    return sb.toString();
  }

  private static byte[] encodeData(Map<String, List<Field>> types, String type, JsonNode data) {
    List<Field> fields = types.get(type);
    if (fields == null) {
      throw new IllegalArgumentException("Unknown type for encodeData: " + type);
    }
    byte[][] parts = new byte[fields.size()][];
    int i = 0;
    for (Field f : fields) {
      parts[i++] = encodeField(types, f.type, data == null ? null : data.get(f.name));
    }
    return concat(parts);
  }

  private static byte[] encodeField(Map<String, List<Field>> types, String type, JsonNode value) {
    String t = type.trim();
    if (t.endsWith("[]")) {
      String inner = t.substring(0, t.length() - 2);
      if (value == null || !value.isArray()) {
        throw new IllegalArgumentException("Expected JSON array for type " + type);
      }
      List<byte[]> chunks = new ArrayList<>();
      for (JsonNode el : value) {
        if (types.containsKey(inner)) {
          chunks.add(encodeData(types, inner, el));
        } else {
          chunks.add(encodeAtomic(inner, el));
        }
      }
      return Hash.keccak256(concat(chunks.toArray(new byte[0][])));
    }
    if (types.containsKey(t)) {
      return hashStruct(types, t, value);
    }
    return encodeAtomic(t, value);
  }

  private static byte[] encodeAtomic(String type, JsonNode value) {
    String t = type.trim();
    if ("string".equals(t)) {
      String s = value == null || value.isNull() ? "" : value.asText();
      return Hash.keccak256(s.getBytes(StandardCharsets.UTF_8));
    }
    if ("bool".equals(t)) {
      BigInteger v = value != null && value.asBoolean() ? BigInteger.ONE : BigInteger.ZERO;
      return uintToBytes32(v);
    }
    if ("address".equals(t)) {
      String hex = value.asText();
      byte[] addr = NumericUtil.hexToBytes(NumericUtil.cleanHexPrefix(hex));
      byte[] word = new byte[32];
      System.arraycopy(addr, 0, word, 32 - addr.length, addr.length);
      return word;
    }
    if ("bytes".equals(t)) {
      byte[] raw;
      if (value == null || value.isNull()) {
        raw = new byte[0];
      } else if (value.isTextual()) {
        String s = value.asText();
        if (NumericUtil.isValidHex(s)) {
          raw = NumericUtil.hexToBytes(NumericUtil.cleanHexPrefix(s));
        } else {
          raw = s.getBytes(StandardCharsets.UTF_8);
        }
      } else {
        raw = new byte[0];
      }
      return Hash.keccak256(raw);
    }
    if (t.startsWith("bytes") && t.length() > 5 && Character.isDigit(t.charAt(5))) {
      int n = Integer.parseInt(t.substring(5));
      byte[] raw = NumericUtil.hexToBytes(NumericUtil.cleanHexPrefix(value.asText()));
      byte[] word = new byte[32];
      System.arraycopy(raw, 0, word, 0, Math.min(n, raw.length));
      return word;
    }
    if (t.startsWith("uint") || t.startsWith("int")) {
      BigInteger v;
      if (value.isIntegralNumber()) {
        v = BigInteger.valueOf(value.longValue());
      } else {
        v = new BigInteger(value.asText(), 10);
      }
      if (t.startsWith("uint")) {
        return uintToBytes32(v);
      }
      return intToBytes32(v);
    }
    throw new IllegalArgumentException("Unsupported atomic type: " + type);
  }

  private static byte[] uintToBytes32(BigInteger v) {
    if (v.signum() < 0) {
      throw new IllegalArgumentException("uint must be non-negative");
    }
    byte[] src = v.toByteArray();
    byte[] out = new byte[32];
    int len = Math.min(32, src.length);
    int srcPos = src.length > 32 ? src.length - 32 : 0;
    System.arraycopy(src, srcPos, out, 32 - len, len);
    return out;
  }

  private static byte[] intToBytes32(BigInteger v) {
    byte[] src = v.toByteArray();
    byte[] out = new byte[32];
    if (src.length >= 32) {
      System.arraycopy(src, src.length - 32, out, 0, 32);
    } else {
      int pad = 32 - src.length;
      if (v.signum() < 0) {
        for (int i = 0; i < pad; i++) {
          out[i] = (byte) 0xff;
        }
      }
      System.arraycopy(src, 0, out, pad, src.length);
    }
    return out;
  }

  private static byte[] concat(byte[]... parts) {
    int len = 0;
    for (byte[] p : parts) {
      len += p.length;
    }
    byte[] out = new byte[len];
    int pos = 0;
    for (byte[] p : parts) {
      System.arraycopy(p, 0, out, pos, p.length);
      pos += p.length;
    }
    return out;
  }

  private static final class Field {
    final String name;
    final String type;

    Field(String name, String type) {
      this.name = name;
      this.type = type;
    }
  }
}
