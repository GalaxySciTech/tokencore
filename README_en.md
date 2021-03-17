<h1 align="center">
  tokencore
</h1>
<p align="center">

  <a href="https://travis-ci.org/paipaipaipai/tokencore">
    <img src="https://travis-ci.org/paipaipaipai/tokencore.svg?branch=master">
  </a>

  <a href="https://github.com/paipaipaipai/tokencore/issues">
    <img src="https://img.shields.io/github/issues/paipaipaipai/tokencore.svg">
  </a>

  <a href="https://github.com/paipaipaipai/tokencore/pulls">
    <img src="https://img.shields.io/github/issues-pr/paipaipaipai/tokencore.svg">
  </a>

  <a href="https://github.com/paipaipaipai/tokencore/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/paipaipaipai/tokencore.svg">
  </a>

  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/paipaipaipai/tokencore.svg">
  </a>
  
</p>

-en [English](README_en.md)
-zh_CN [Simplified Chinese](README.md)

# tokencore introduction

##### The core components of the blockchain wallet backend, support BTC, OMNI, ETH, ERC20, TRX, TRC20, BCH, BSV, DOGE, DASH, LTC

# tokencore usage


#### Introducing this library
gradle way
In your build.gradle
```
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        compile 'com.github.paipaipaipai:tokencore:1.1.1'
    }
```

maven way
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
	<dependency>
	    <groupId>com.github.paipaipaipai</groupId>
	    <artifactId>tokencore</artifactId>
	    <version>1.1.1</version>
	</dependency>
```
#### Initialize identity
```java
    try {
        Files.createDirectories(Paths.get("${keyStoreProperties.dir}/wallets"))
    } catch (Throwable ignored) {
    }
//KeystoreStorage is an interface that implements its getdir method
    WalletManager.storage = KeystoreStorage();
    WalletManager.scanWallets();
    String password = "123456";
    Identity identity = Identity.GetCurrentIdentity ();
    if (identity == null) {
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
    Identity identity = Identity.GetCurrentIdentity ()
    String password ="123456";
    List<String> chainTypes = new ArrayList();
    chainTypes.add(ChainType.BITCOIN);

    List<Wallet> wallets = identity.DeriveWalletsByMnemonics (
            chainTypes,
    password,
    MnemonicUtil.randomMnemonicCodes()
    );

```

#### Offline signature

btc

```java
        String password="123456";
        String toAddress="33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9";
        int changeIdx=0;
        long amount=1000L;
        long fee=555L;
        //utxos needs to go to the node or external api to get
        ArrayList<UTXO> utxos=new ArrayList();
        BitcoinTransaction bitcoinTransaction=BitcoinTransaction(
        toAddress,
        changeIdx,
        amount,
        fee,
        utxos
        );
        TxSignResult txSignResult=bitcoinTransaction.signTransaction(
        ChainId.BITCOIN_MAINNET.toString(),
        password,
        wallet
        );


        //Online broadcast
        rpc.sendRawTransaction(txSignResult.getSignedTx());
```

tron

```java
        String from="TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
        String to="TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";
        long amount=1;
        String password="123456";

        TronTransaction transaction=new TronTransaction(from,to,amount);
        //Offline signature, it is not recommended to sign and broadcast together
        TxSignResult txSignResult=transaction.signTransaction(String.valueOf(ChainId.BITCOIN_MAINNET),password,wallet);



        //Online broadcast
        ObjectMapper obj=new ObjectMapper();

        TronClient client=TronClient.ofMainnet("3333333333333333333333333333333333333333333333333333333333333333");

        client.blockingStub.broadcastTransaction(obj.readValue(txSignResult.getSignedTx(),Transaction.class));  
```

#### Note: This is just a functional component of a digital currency! ! ! It is only for learning and does not provide complete blockchain business functions.

