<h1 align="center">Tokencore</h1>

<p align="center">
  <strong>Multi-chain cryptocurrency wallet core library for Java</strong>
</p>

<p align="center">
  <a href="https://github.com/galaxyscitech/tokencore/actions">
    <img src="https://github.com/galaxyscitech/tokencore/actions/workflows/ci.yml/badge.svg" alt="Build Status">
  </a>
  <a href="https://jitpack.io/#galaxyscitech/tokencore">
    <img src="https://jitpack.io/v/galaxyscitech/tokencore.svg" alt="JitPack">
  </a>
</p>

<p align="center">
  <a href="#quick-start-30-seconds">Quick Start (30s)</a> &nbsp;&bull;&nbsp;
  <a href="#integration">Integration</a> &nbsp;&bull;&nbsp;
  <a href="#core-features-recommended-minimum">Recommended Minimum</a> &nbsp;&bull;&nbsp;
  <a href="#supported-chains">Supported Chains</a>
</p>

---

## Introduction

Tokencore is a lightweight Java library for wallet fundamentals: HD derivation, encrypted keystore management, and offline signing.

If your goal is "install and use immediately", start with the 30-second quick start below and only enable additional chains/features later.

## Quick Start (30 seconds)

### 1) Add dependency

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.galaxyscitech:tokencore:1.3.0'
}
```

### 2) Copy this minimal bootstrap code

```java
WalletManager.storage = () -> new File("./keystore");
WalletManager.scanWallets();

String password = "change_me";
Identity identity = Identity.getCurrentIdentity();
if (identity == null) {
    identity = Identity.createIdentity("default", password, "", Network.MAINNET, Metadata.P2WPKH);
}

Wallet wallet = identity.deriveWalletByMnemonics(
    ChainType.ETHEREUM,
    password,
    MnemonicUtil.randomMnemonicCodes());

System.out.println("Address = " + wallet.getAddress());
```

### 3) Verify locally

```bash
./gradlew test
```

## Core Features (Recommended Minimum)

For new integrators, keep the initial rollout small:

1. **Identity + keystore only** (account generation + secure storage)
2. **Single chain first** (recommend: ETH or BTC)
3. **Offline signing only** (avoid online key usage)
4. **No multi-chain abstraction in v1 API surface**

This reduces integration complexity and speeds up first successful deployment.

## Integration

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

## Supported Chains

| Chain | Token Standards | Features |
|-------|----------------|----------|
| **Bitcoin** | BTC, OMNI | UTXO management, SegWit (P2WPKH) |
| **Ethereum** | ETH, ERC-20 | Offline signing, nonce management |
| **TRON** | TRX, TRC-20 | Transaction signing |
| **Bitcoin Cash** | BCH | CashAddr format |
| **Bitcoin SV** | BSV | Transaction signing |
| **Litecoin** | LTC | Transaction signing |
| **Dogecoin** | DOGE | Transaction signing |
| **Dash** | DASH | Transaction signing |
| **Filecoin** | FIL | Transaction signing |

## Build & Test

```bash
./gradlew build
./gradlew test
```

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).
