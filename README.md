<h1 align="center">Tokencore</h1>

<p align="center">
  <a href="https://travis-ci.com/galaxyscitech/tokencore">
    <img src="https://travis-ci.com/galaxyscitech/tokencore.svg?branch=master" alt="Build Status">
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

## Languages

- English: [README](README.md)

## Use Cases

This serves as the exchange wallet backend. For more details, check out [java-wallet](https://github.com/galaxyscitech/java-wallet).

## Introduction

Tokencore is a central component for blockchain wallet backends. It currently supports the following:

- BTC, OMNI, ETH, ERC20
- TRX, TRC20, BCH, BSV
- DOGE, DASH, LTC, FILECOIN

## Integration

### Gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    compile 'com.github.galaxyscitech:tokencore:1.2.7'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>tronj</id>
        <url>https://dl.bintray.com/tronj/tronj</url>
    </repository>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.galaxyzxcv</groupId>
    <artifactId>tokencore</artifactId>
    <version>1.2.7</version>
</dependency>
```

## Sample Test

View a sample test at [Tokencore Test Sample](https://github.com/galaxyscitech/tokencore/blob/master/src/test/java/org/consenlabs/tokencore/Test.java).

## Usage Guide

### Initialize Identity

```java
try {
    Files.createDirectories(Paths.get("${keyStoreProperties.dir}/wallets"));
} catch(Throwable ignored) {}

WalletManager.storage = new KeystoreStorage();
WalletManager.scanWallets();
String password = "123456";
Identity identity = Identity.getCurrentIdentity();

if(identity == null) {
    Identity.createIdentity("token", password, "", Network.MAINNET, Metadata.P2WPKH);
}
```

### Generate Wallet

```java
Identity identity = Identity.getCurrentIdentity();
String password = "123456";
Wallet wallet = identity.deriveWalletByMnemonics(ChainType.BITCOIN, password, MnemonicUtil.randomMnemonicCodes());
System.out.println(wallet.getAddress());
```

## Offline Signature

Offline signing refers to the process of creating a digital signature for a transaction without connecting to the internet. This method enhances security by ensuring private keys never come in contact with an online environment. Here's how you can create an offline signature with Tokencore for Bitcoin and TRON:

### Bitcoin

1. **Set Up Transaction Details**

   Define the details of your Bitcoin transaction, including recipient's address, change index, amount to be transferred, and the fee.

   ```java
   String password = "123456";
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

   With all the details in place, initialize the Bitcoin transaction and sign it offline.

   ```java
   BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(toAddress, changeIdx, amount, fee, utxos);
   Wallet wallet = WalletManager.findWalletByAddress(ChainType.BITCOIN, "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9");
   TxSignResult txSignResult = bitcoinTransaction.signTransaction(String.valueOf(ChainId.BITCOIN_MAINNET), password, wallet);
   System.out.println(txSignResult);
   ```

### TRON

1. **Set Up Transaction Details**

   Define your TRON transaction details, including the sender's address, recipient's address, and amount.

   ```java
   String from = "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
   String to = "TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";
   long amount = 1;
   String password = "123456";
   ```

2. **Initialize Transaction & Sign**

   Once you have the transaction details, initialize the TRON transaction and sign it offline.

   ```java
   TronTransaction transaction = new TronTransaction(from, to, amount);
   Wallet wallet = WalletManager.findWalletByAddress(ChainType.BITCOIN, "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8");
   TxSignResult txSignResult = transaction.signTransaction(String.valueOf(ChainId.BITCOIN_MAINNET), password, wallet);
   System.out.println(txSignResult);
   ```

Remember, offline signing enhances security but requires a thorough understanding of transaction construction to avoid errors.

> **Note**: Tokencore is a functional component for digital currency. It's primarily for learning purposes and doesn't offer a complete blockchain business suite.
