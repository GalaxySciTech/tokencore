<h1 align="center">
  tokencore
</h1>
<p align="center">

  <a href="https://travis-ci.org/pai01234/tokencore">
    <img src="https://travis-ci.org/pai01234/tokencore.svg?branch=master">
  </a>

  <a href="https://github.com/pai01234/tokencore/issues">
    <img src="https://img.shields.io/github/issues/pai01234/tokencore.svg">
  </a>

  <a href="https://github.com/pai01234/tokencore/pulls">
    <img src="https://img.shields.io/github/issues-pr/pai01234/tokencore.svg">
  </a>

  <a href="https://github.com/pai01234/tokencore/graphs/contributors">
    <img src="https://img.shields.io/github/contributors/pai01234/tokencore.svg">
  </a>

  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/pai01234/tokencore.svg">
  </a>

</p>

- en [English](README_en.md)
- zh_CN [简体中文](README.md)

# tokencore介绍

##### 区块链钱包后台核心组件，支持BTC,OMNI,ETH,ERC20,TRX,TRC20,BCH,BSV,DOGE,DASH,LTC

# tokencore使用方式

#### 引入本库
gradle方式
在你的build.gradle里面
```
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        compile 'com.github.pai01234:tokencore:1.0.8.3'
    }
```

#### 初始化身份
```java
    try {
        Files.createDirectories(Paths.get("${keyStoreProperties.dir}/wallets"))
        } catch (Throwable ignored) {
        }
//KeystoreStorage是接口，实现它的getdir方法
        WalletManager.storage = KeystoreStorage();
        WalletManager.scanWallets();
        String password = "123456";
        Identity identity = Identity . getCurrentIdentity ();
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

#### 生成钱包

```java
    Identity identity = Identity . getCurrentIdentity ()
        String password ="123456";
        List<String> chainTypes = new ArrayList();
        chainTypes.add(ChainType.BITCOIN);

        List<Wallet> wallets = identity . deriveWalletsByMnemonics (
        chainTypes,
        password,
        MnemonicUtil.randomMnemonicCodes()
        );

```

#### 离线签名

```java
    String password ="123456";
        String toAddress ="dsadsadsadsa";
        int changeIdx =0;
        long amount =1000L;
        long fee =555L;
//utxos需要去节点或者外部api获取
        ArrayList<UTXO> utxos = new ArrayList();
        BitcoinTransaction bitcoinTransaction = BitcoinTransaction (
        toAddress,
        changeIdx,
        amount,
        fee,
        utxos
        );
        TxSignResult txSignResult = bitcoinTransaction . signTransaction (
        ChainId.BITCOIN_MAINNET.toString(),
        password,
        wallet
        );
```

#### 注意：这只是一个数字货币的功能组件！！！只供学习使用，不提供完整的区块链业务功能

本人不提供联系方式，有问题提交issue，暂时不合作 本人最近觉得做技术赚的不多还很累，所以打算开源区块链钱包后台的代码，但是最近okex的号被盗了资产清零了，我现在很惆怅。

所以我提供以下数字货币钱包，大家如果觉得软件对您有帮助的话，可以往下面地址转币进行捐赠

比特币

1DVEEbd3JFhM4RCXk7p3nSjyrgDRdfbHpZ

以太坊

0x19788062a1057b2258622b53912d7d6c726bf736

波场

TWfFoVLKTA1LDkGDRW7yCQv8uhPKHrzHk1

捐赠完至少50usdt后可以截图交易，在tokencore库里面提交issue,并且提供自己联系方式，我核实后会联系您，可以考虑提供技术援助

