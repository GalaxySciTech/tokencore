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
  <a href="https://github.com/galaxyscitech/tokencore/issues">
    <img src="https://img.shields.io/github/issues/galaxyscitech/tokencore.svg" alt="Issues">
  </a>
  <a href="https://github.com/galaxyscitech/tokencore/pulls">
    <img src="https://img.shields.io/github/issues-pr/galaxyscitech/tokencore.svg" alt="Pull Requests">
  </a>
  <a href="https://github.com/galaxyscitech/tokencore/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/galaxyscitech/tokencore.svg" alt="Contributors">
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/galaxyscitech/tokencore.svg" alt="License">
  </a>
</p>

<p align="center">
  <a href="#supported-chains">Supported Chains</a> &nbsp;&bull;&nbsp;
  <a href="#quick-start">Quick Start</a> &nbsp;&bull;&nbsp;
  <a href="#integration">Integration</a> &nbsp;&bull;&nbsp;
  <a href="#offline-signing">Offline Signing</a> &nbsp;&bull;&nbsp;
  <a href="#contact">Contact</a>
</p>

---

## Introduction

Tokencore is a lightweight Java library that provides core wallet functionality for multiple blockchains. It handles HD wallet derivation, encrypted keystore management, and offline transaction signing — making it the ideal building block for exchange backends and custodial wallet services.

For a complete exchange wallet backend built on top of Tokencore, see [java-wallet](https://github.com/galaxyscitech/java-wallet).

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

## Requirements

- **Java** 8 or higher
- **Gradle** 8.5+ (included via wrapper, no manual install needed)

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

## Quick Start

### 1. Initialize Keystore & Identity

```java
WalletManager.storage = new KeystoreStorage() {
    @Override
    public File getKeystoreDir() {
        return new File("/path/to/keystore");
    }
};
WalletManager.scanWallets();

String password = "your_password";
Identity identity = Identity.getCurrentIdentity();

if (identity == null) {
    identity = Identity.createIdentity(
        "token", password, "", Network.MAINNET, Metadata.P2WPKH);
}
```

### 2. Derive a Wallet

```java
Identity identity = Identity.getCurrentIdentity();
Wallet wallet = identity.deriveWalletByMnemonics(
    ChainType.BITCOIN, "your_password", MnemonicUtil.randomMnemonicCodes());
System.out.println(wallet.getAddress());
```

## Offline Signing

Offline signing creates a digital signature without ever exposing private keys to an online environment.

### Bitcoin

```java
// 1. Define transaction parameters
String toAddress = "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9";
int changeIdx = 0;
long amount = 1000L;
long fee = 555L;

// 2. Collect UTXOs (from your node or a third-party API)
ArrayList<BitcoinTransaction.UTXO> utxos = new ArrayList<>();

// 3. Build and sign
BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(
    toAddress, changeIdx, amount, fee, utxos);
Wallet wallet = WalletManager.findWalletByAddress(
    ChainType.BITCOIN, "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9");
TxSignResult txSignResult = bitcoinTransaction.signTransaction(
    String.valueOf(ChainId.BITCOIN_MAINNET), "your_password", wallet);
System.out.println(txSignResult.getSignedTx());
```

### Ethereum

```java
EthereumTransaction tx = new EthereumTransaction(
    BigInteger.ZERO,                                    // nonce
    BigInteger.valueOf(20_000_000_000L),                // gasPrice
    BigInteger.valueOf(21_000),                         // gasLimit
    "0xRecipientAddress",                               // to
    BigInteger.valueOf(1_000_000_000_000_000_000L),     // value (1 ETH)
    ""                                                  // data
);

Wallet wallet = WalletManager.findWalletByAddress(
    ChainType.ETHEREUM, "0xYourAddress");
TxSignResult result = tx.signTransaction(
    String.valueOf(ChainId.ETHEREUM_MAINNET), "your_password", wallet);
System.out.println(result.getSignedTx());
```

### TRON

```java
String from = "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
String to   = "TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";

TronTransaction transaction = new TronTransaction(from, to, 1L);
Wallet wallet = WalletManager.findWalletByAddress(ChainType.TRON, from);
TxSignResult result = transaction.signTransaction(
    "mainnet", "your_password", wallet);
System.out.println(result.getSignedTx());
```

## Build & Test

```bash
# Build the library
./gradlew build

# Run the test suite
./gradlew test
```

## Project Structure

```
src/main/java/org/consenlabs/tokencore/
├── wallet/
│   ├── Identity.java          # HD identity management
│   ├── Wallet.java            # Wallet abstraction
│   ├── WalletManager.java     # Wallet lifecycle & discovery
│   ├── address/               # Chain-specific address generation
│   ├── keystore/              # Encrypted keystore implementations
│   ├── model/                 # ChainType, ChainId, Metadata, etc.
│   ├── network/               # Bitcoin-fork network parameters
│   ├── transaction/           # Offline signing per chain
│   └── validators/            # Address & key validation
└── foundation/
    ├── crypto/                # AES, KDF, hashing primitives
    ├── utils/                 # Mnemonic, numeric, byte helpers
    └── rlp/                   # RLP encoding (Ethereum)
```

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

## Contact

- **Telegram**: [t.me/GalaxySciTech](https://t.me/GalaxySciTech)
- **Website**: [galaxy.doctor](https://galaxy.doctor)
- **GitHub Issues**: [Report a bug](https://github.com/galaxyscitech/tokencore/issues/new)

---

> **Disclaimer**: Tokencore is a functional component for digital currency operations. It is intended primarily for learning and development purposes and does not provide a complete blockchain business solution. Use at your own risk.
