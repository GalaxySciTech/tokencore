# Tokencore

Tokencore is a Java multi-chain wallet core library for exchange backends, custody systems, and wallet services.

## What Tokencore provides

- Multi-chain address generation
- HD wallet derivation and mnemonic workflows
- Encrypted keystore management
- Offline transaction signing for major chain families

Supported chains include:
- **EVM**: Ethereum
- **Bitcoin family**: Bitcoin, Litecoin, Dogecoin, Dash, Bitcoin Cash, Bitcoin SV
- **Others**: TRON, Filecoin, EOS

---

## Core Features (Recommended Minimum)

- Java 8+
- Gradle wrapper included (`./gradlew`)

---

## Install

### Gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.galaxyscitech:tokencore:1.3.0'
}
```

### Maven

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.galaxyscitech</groupId>
  <artifactId>tokencore</artifactId>
  <version>1.3.0</version>
</dependency>
```

---

## Quick start (runnable)

```java
import org.consenlabs.tokencore.foundation.utils.MnemonicUtil;
import org.consenlabs.tokencore.wallet.*;
import org.consenlabs.tokencore.wallet.model.*;

import java.io.File;

public class QuickStart {
  public static void main(String[] args) {
    WalletManager.storage = () -> new File("./keystore");
    WalletManager.scanWallets();

    String password = "UseAStrongPassword_123";
    Identity identity = Identity.getCurrentIdentity();
    if (identity == null) {
      identity = Identity.createIdentity("main", password, "", Network.MAINNET, Metadata.P2WPKH);
    }

    Wallet ethWallet = identity.deriveWalletByMnemonics(
      ChainType.ETHEREUM,
      password,
      MnemonicUtil.randomMnemonicCodes()
    );

    Wallet btcWallet = identity.deriveWalletByMnemonics(
      ChainType.BITCOIN,
      password,
      MnemonicUtil.randomMnemonicCodes()
    );

    System.out.println("ETH address: " + ethWallet.getAddress());
    System.out.println("BTC address: " + btcWallet.getAddress());
  }
}
```

---

## Common usage

### 1) Import wallet from private key

```java
Metadata metadata = new Metadata();
metadata.setChainType(ChainType.ETHEREUM);
metadata.setSource(Metadata.FROM_PRIVATE);
metadata.setNetwork(Network.MAINNET);

Wallet wallet = WalletManager.importWalletFromPrivateKey(
  metadata,
  "4c0883a69102937d6231471b5dbb6204fe512961708279f14a15c89a7e5a5c3c",
  "password123",
  true
);
```

### 2) Import wallet from mnemonic

```java
Metadata metadata = new Metadata();
metadata.setChainType(ChainType.DOGECOIN);
metadata.setSource(Metadata.FROM_MNEMONIC);
metadata.setNetwork(Network.MAINNET);
metadata.setSegWit(Metadata.NONE);

Wallet wallet = WalletManager.importWalletFromMnemonic(
  metadata,
  "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
  BIP44Util.DOGECOIN_MAINNET_PATH,
  "password123",
  true
);
```

### 3) Find wallet by mnemonic (BTC-family friendly)

```java
Wallet wallet = WalletManager.findWalletByMnemonic(
  ChainType.DOGECOIN,
  Network.MAINNET,
  "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
  BIP44Util.DOGECOIN_MAINNET_PATH,
  Metadata.NONE
);
```

### 4) Export keystore and recover by keystore

```java
String keystoreJson = WalletManager.exportKeystore(wallet.getId(), "password123");
Wallet found = WalletManager.findWalletByKeystore(ChainType.ETHEREUM, keystoreJson, "password123");
```

---

## Security recommendations

- Never log or print private keys, mnemonics, or decrypted keystore payloads.
- Keep signing in isolated/offline environments whenever possible.
- Use strong passwords and avoid hardcoded secrets.
- Consider HSM/KMS for production secret governance.
- Enforce strict access controls around keystore files.

---

## Typical errors

- `password_incorrect`
- `mnemonic_length_invalid`
- `mnemonic_word_invalid`
- `invalid_mnemonic_path`
- `unsupported_chain`
- `private_key_address_not_match`

---

## Build and test

```bash
./gradlew test
./gradlew build
```

CI runs on Java 8/11/17 via GitHub Actions.
