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

# README.md
- en [English](README_en.md)

- zh_CN [Simplified Chinese](README.md)

# tokencore introduction

##### The core components of the blockchain wallet backend, support BTC, OMNI, ETH, ERC20, TRX, TRC20, BCH, BSV, DOGE, DASH, LTC

# tokencore usage

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
        Identity identity = Identity.getCurrentIdentity();
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
Identity identity = Identity.getCurrentIdentity()
String password="123456";
List<String> chainTypes=new ArrayList();
chainTypes.add(ChainType.BITCOIN);

List<Wallet> wallets=identity.deriveWalletsByMnemonics(
            chainTypes,
            password,
            MnemonicUtil.randomMnemonicCodes()
        );

```

#### Offline signature

```java
String password="123456";
String toAddress="dsadsadsadsa";
int changeIdx=0;
long amount=1000L;
long fee=555L;
//utxos needs to go to the node or external api to get
ArrayList<UTXO> utxos=new ArrayList();
BitcoinTransaction bitcoinTransaction = BitcoinTransaction(
            toAddress,
            changeIdx,
            amount,
            fee,
            utxos
        );
TxSignResult txSignResult = bitcoinTransaction.signTransaction(
            ChainId.BITCOIN_MAINNET.toString(),
            password,
            wallet
        );
```

#### Note: This is just a functional component of a digital currency! ! ! It is only for learning and does not provide complete blockchain business functions. If you need business backends, look down

# The project described below is not open source and has nothing to do with this project
### Blockchain server introduction:
- #### The business backend is a more powerful business system constructed based on the functional components of the digital currency, which can obtain different public chain blockchain addresses at any time, and supports (BTC, OMNI, ETH, ERC20, TRX, TRC20) , BCH, BSV, DOGE, DASH, LTC) deposit and withdrawal functions

 
- ##### This is the usage document of swagger-ui
 ![](https://i.ibb.co/CK9VHpF/We-Chatff11cad89ae03d68aacde5f83c62d63a.png)


- ##### This is the architecture diagram I use
 ![](https://i.ibb.co/KrpJwDG/1590596278351.jpg)


- ##### This is a screenshot of the database flow (to protect some privacy, code to watch)
![](https://i.ibb.co/3dR8tpn/1590596939623.jpg)



#### Complete system architecture situation:
- Use springboot framework
- The language is java, kotlin
- Use rabbitmq message queue
- mysql cloud database
- xxl-job distributed timing task framework

#### The entire system is developed by myself, and has run hundreds of millions of dollars in the production environment. It has sufficient reliability, scalability, and practicality, and can be used with confidence.


### Cooperation contact WeChat: pai01234

## pay attention
Anyone who uses this source code to engage in commercial activities and causes losses to others and yourself, I am not responsible for it!
