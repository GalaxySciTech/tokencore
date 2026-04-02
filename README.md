<h1 align="center">Tokencore</h1>

<p align="center">
  <a href="https://github.com/galaxyscitech/tokencore/actions">
    <img src="https://github.com/galaxyscitech/tokencore/actions/workflows/ci.yml/badge.svg" alt="Build Status">
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

## Contact

- [GalaxySciTech](https://galaxy.doctor) - Official Website

## Use Cases

This serves as the exchange wallet backend. For more details, check out [java-wallet](https://github.com/galaxyscitech/java-wallet).

## Introduction

Tokencore is a central component for blockchain wallet backends. It currently supports the following:

- BTC, OMNI, ETH, ERC20
- TRX, TRC20, BCH, BSV
- DOGE, DASH, LTC, FILECOIN

## Requirements

- Java 8+
- Gradle 8.5+ (included via wrapper)

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

### Initialize Identity

```java
try {
    Files.createDirectories(Paths.get("${keyStoreProperties.dir}/wallets"));
} catch(Throwable ignored) {}

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
    Identity.createIdentity("token", password, "", Network.MAINNET, Metadata.P2WPKH);
}
```

### Generate Wallet

```java
Identity identity = Identity.getCurrentIdentity();
String password = "your_password";
Wallet wallet = identity.deriveWalletByMnemonics(
    ChainType.BITCOIN, password, MnemonicUtil.randomMnemonicCodes());
System.out.println(wallet.getAddress());
```

## Offline Signature

Offline signing refers to the process of creating a digital signature for a transaction without connecting to the internet. This method enhances security by ensuring private keys never come in contact with an online environment.

### Bitcoin

1. **Set Up Transaction Details**

   ```java
   String password = "your_password";
   String toAddress = "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9";
   int changeIdx = 0;
   long amount = 1000L;
   long fee = 555L;
   ```

2. **Fetch UTXOs**

   You'll need UTXOs (Unspent Transaction Outputs) for the transaction. Usually, these are fetched from a node or an external API.

   ```java
   ArrayList<BitcoinTransaction.UTXO> utxos = new ArrayList<>();
   ```

3. **Initialize Transaction & Sign**

   ```java
   BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(
       toAddress, changeIdx, amount, fee, utxos);
   Wallet wallet = WalletManager.findWalletByAddress(
       ChainType.BITCOIN, "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9");
   TxSignResult txSignResult = bitcoinTransaction.signTransaction(
       String.valueOf(ChainId.BITCOIN_MAINNET), password, wallet);
   System.out.println(txSignResult.getSignedTx());
   ```

### TRON

1. **Set Up Transaction Details**

   ```java
   String from = "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
   String to = "TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";
   long amount = 1;
   String password = "your_password";
   ```

2. **Initialize Transaction & Sign**

   ```java
   TronTransaction transaction = new TronTransaction(from, to, amount);
   Wallet wallet = WalletManager.findWalletByAddress(
       ChainType.TRON, "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8");
   TxSignResult txSignResult = transaction.signTransaction(
       "mainnet", password, wallet);
   System.out.println(txSignResult.getSignedTx());
   ```

### Ethereum

```java
EthereumTransaction tx = new EthereumTransaction(
    BigInteger.ZERO,                          // nonce
    BigInteger.valueOf(20_000_000_000L),      // gasPrice
    BigInteger.valueOf(21000),                // gasLimit
    "0xRecipientAddress",                     // to
    BigInteger.valueOf(1_000_000_000_000_000_000L), // value (1 ETH)
    ""                                         // data
);

Wallet wallet = WalletManager.findWalletByAddress(
    ChainType.ETHEREUM, "0xYourAddress");
TxSignResult result = tx.signTransaction(
    String.valueOf(ChainId.ETHEREUM_MAINNET), password, wallet);
System.out.println(result.getSignedTx());
```

## Running Tests

```bash
./gradlew test
```

## Building

```bash
./gradlew build
```

> **Note**: Tokencore is a functional component for digital currency. It's primarily for learning purposes and doesn't offer a complete blockchain business suite.
