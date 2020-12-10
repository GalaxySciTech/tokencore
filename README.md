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

#### 注意：这只是一个数字货币的功能组件！！！只供学习使用，不提供完整的区块链业务功能，如果需要业务后台的往下看

# 以下介绍的项目不开源，并与本项目无关
### 区块链java wallet介绍：
- #### 业务后台是依据该数字货币的功能组件所构建出来的更强大的业务系统，可以随时获取不同公链区块链地址,并支持(BTC,OMNI,ETH,ERC20,TRX,TRC20,BCH,BSV,DOGE,DASH,LTC)的充归提功能

- ###### 后台管理演示
![](https://i.ibb.co/zb8LtyH/test.gif)
- ###### API接口演示
![](https://i.ibb.co/MPbh9Gj/test1.gif)

#### 全套系统架构情况：
- 使用springboot框架
- 语言为java,kotlin
- 使用rabbitmq消息队列
- mysql云数据库
- xxl-job分布式定时任务框架

#### 全套系统均由我一个人开发，并且已经在生产环境跑了上亿流水，具有充足的可靠性，可扩展性，实用性，可以放心使用。


### 合作联系微信： pai01234

## 特别注意
任何使用本源码从事商业活动，对别人和自己造成损失的，本人概不负责！
