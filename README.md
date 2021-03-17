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

- en [English](README_en.md)
- zh_CN [简体中文](README.md)

# tokencore介绍

##### 区块链钱包后台核心组件，支持BTC,OMNI,ETH,ERC20,TRX,TRC20,BCH,BSV,DOGE,DASH,LTC

# tokencore使用方式

#### 引入本库

gradle方式 在你的build.gradle里面

```
    repositories {
        maven { url 'https://jitpack.io' }
    }
    
    dependencies {
        compile 'com.github.paipaipaipai:tokencore:1.1.1'
    }
```

maven方式

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

#### 初始化身份

```java
    try{
        Files.createDirectories(Paths.get("${keyStoreProperties.dir}/wallets"))
        }catch(Throwable ignored){
        }
        //KeystoreStorage是接口，实现它的getdir方法
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

#### 生成钱包

```java
        Identity identity=Identity.getCurrentIdentity()
        String password="123456";
        List<String> chainTypes=new ArrayList();
        chainTypes.add(ChainType.BITCOIN);

        List<Wallet> wallets=identity.deriveWalletsByMnemonics(
        chainTypes,
        password,
        MnemonicUtil.randomMnemonicCodes()
        );

```

#### 离线签名

比特币

```java
        String password="123456";
        String toAddress="33sXfhCBPyHqeVsVthmyYonCBshw5XJZn9";
        int changeIdx=0;
        long amount=1000L;
        long fee=555L;
        //utxos需要去节点或者外部api获取
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

        //在线广播
        rpc.sendRawTransaction(txSignResult.getSignedTx());
```

波场

```java
        String from="TJRabPrwbZy45sbavfcjinPJC18kjpRTv8";
        String to="TF17BgPaZYbz8oxbjhriubPDsA7ArKoLX3";
        long amount=1;
        String password="123456";

        TronTransaction transaction=new TronTransaction(from,to,amount);
        //离线签名，不建议签名和广播放一块
        TxSignResult txSignResult=transaction.signTransaction(String.valueOf(ChainId.BITCOIN_MAINNET),password,wallet);


        //在线广播
        ObjectMapper obj=new ObjectMapper();

        TronClient client=TronClient.ofMainnet("3333333333333333333333333333333333333333333333333333333333333333");

        client.blockingStub.broadcastTransaction(obj.readValue(txSignResult.getSignedTx(),Transaction.class));  
```

#### 注意：这只是一个数字货币的功能组件！！！只供学习使用，不提供完整的区块链业务功能

本人不提供联系方式，有问题提交issue