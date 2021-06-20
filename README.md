<h1 align="center">
  tokencore
</h1>
<p align="center">

  <a href="https://travis-ci.org/lailaibtc/tokencore">
    <img src="https://travis-ci.org/lailaibtc/tokencore.svg?branch=master">
  </a>

  <a href="https://github.com/lailaibtc/tokencore/issues">
    <img src="https://img.shields.io/github/issues/lailaibtc/tokencore.svg">
  </a>

  <a href="https://github.com/lailaibtc/tokencore/pulls">
    <img src="https://img.shields.io/github/issues-pr/lailaibtc/tokencore.svg">
  </a>

  <a href="https://github.com/lailaibtc/tokencore/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/lailaibtc/tokencore.svg">
  </a>

  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/lailaibtc/tokencore.svg">
  </a>
  
</p>

# contact details

- [My Telegram](https://t.me/lailaibtc) / @Telegram
  
If you need the [java-wallet](https://github.com/paipaipaipai/java-wallet) wallet backend source code or build, you can directly add me to a private chat

# language selection

- en [English](README.md)
- zh_CN [简体中文](README_zh_CN.md)

# tokencore introduction

##### The core components of the blockchain wallet backend, support BTC, OMNI, ETH, ERC20, TRX, TRC20, BCH, BSV, DOGE, DASH, LTC,FILECOIN

# tokencore usage


#### Introducing this library
- gradle way
In your build.gradle
```
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        compile 'com.github.lailaibtc:tokencore:1.2.7'
    }
```

- maven way
```
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
	    <groupId>com.github.lailaibtc</groupId>
	    <artifactId>tokencore</artifactId>
	    <version>1.2.7</version>
	</dependency>
```
#### Test sample
[https://github.com/paipaipaipai/tokencore/blob/master/src/test/java/org/consenlabs/tokencore/Test.java](https://github.com/paipaipaipai/tokencore/blob/master/ src/test/java/org/consenlabs/tokencore/Test.java)
#### Initialize identity

```java
    try{
        Files.createDirectories(Paths.get("${keyStoreProperties.dir}/wallets"))
        }catch(Throwable ignored){
        }
        //KeystoreStorage is an interface that implements its getdir method
        WalletManager.storage=KeystoreStorage();
        WalletManager.scanWallets();
        String password="123456";
        Identity identity=Identity.getCurrentIdentity();
        if(identity==null){
        Identity.createIdentity(
        "token",
        password,
        "",
        Network.MAINNET,
        Metadata.P2WPKH
        );
        }
```

#### Generate wallet

```java
        Identity identity = Identity.getCurrentIdentity();
        String password = "123456";
        Wallet wallet = identity.deriveWalletByMnemonics(
        ChainType.BITCOIN,
        password,
        MnemonicUtil.randomMnemonicCodes()
        );
        System.out.println(wallet.getAddress());

```

#### Offline signature

- Bitcoin

```java
        String password = "123456";
        String toAddress = "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9";
        int changeIdx = 0;
        long amount = 1000L;
        long fee = 555L;
        //utxos needs to go to the node or external api to get
        ArrayList<BitcoinTransaction.UTXO> utxos = new ArrayList();
        BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(
        toAddress,
        changeIdx,
        amount,
        fee,
        utxos
        );
        Wallet wallet = WalletManager.findWalletByAddress(ChainType.BITCOIN, "33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9");
        TxSignResult txSignResult = bitcoinTransaction.signTransaction(
        String.valueOf(ChainId.BITCOIN_MAINNET),
        password,
        wallet
        );
        System.out.println(txSignResult);
```

- TRON

```java
        String from = "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
        String to = "TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";
        long amount = 1;
        String password = "123456";
        Wallet wallet = WalletManager.findWalletByAddress(ChainType.BITCOIN, "TJRabPrwbZy45sbavfcjinPJC18kjpRTv8");
        TronTransaction transaction = new TronTransaction(from, to, amount);
        //Offline signature, it is not recommended to sign and broadcast together
        TxSignResult txSignResult = transaction.signTransaction(String.valueOf(ChainId.BITCOIN_MAINNET), password, wallet);

        System.out.println(txSignResult);
```

#### Note: This is just a functional component of a digital currency! ! ! It is only for learning and does not provide complete blockchain business functions
