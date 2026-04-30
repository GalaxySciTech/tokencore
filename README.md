# Tokencore

Java 多链钱包核心库，面向交易所、托管钱包、钱包后端服务。

## 核心能力
- 多链地址生成（BTC / ETH / TRON / BCH / BSV / LTC / DOGE / DASH / FIL）
- HD Wallet / Mnemonic 导入导出
- Keystore 加密管理
- 多链离线签名（避免在线明文私钥）

## 快速开始（可直接跑通）

### 1. 依赖
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.galaxyscitech:tokencore:1.3.0'
}
```

### 2. 初始化并生成地址
```java
WalletManager.storage = () -> new File("./keystore");
WalletManager.scanWallets();

String password = "UseAStrongPassword_123";
Identity identity = Identity.getCurrentIdentity();
if (identity == null) {
    identity = Identity.createIdentity("exchange-main", password, "", Network.MAINNET, Metadata.P2WPKH);
}

Wallet ethWallet = identity.deriveWalletByMnemonics(
    ChainType.ETHEREUM, password, MnemonicUtil.randomMnemonicCodes());

Wallet btcWallet = identity.deriveWalletByMnemonics(
    ChainType.BITCOIN, password, MnemonicUtil.randomMnemonicCodes());
```

## 链路示例
- BTC: 使用 `BitcoinTransaction` 离线签名 UTXO 交易。
- ETH: 使用 `EthereumTransaction` 构建并签名转账。
- TRON: 使用 `TronTransaction` 构建并签名。

> 可参考 `src/test/java/.../EthereumTransactionTest.java` 与 `WalletManagerTest.java` 的可运行样例。

## 安全注意事项（强制建议）
1. 不要在日志中打印私钥、助记词、keystore 明文。
2. 不要把密码硬编码到仓库。
3. 生产环境建议 HSM / KMS 托管密码和主密钥。
4. 私钥相关变量使用后尽快释放引用，避免长生命周期缓存。
5. 所有签名应在离线或受控环境执行。

## 常见错误
- `password_incorrect`: 密码错误。
- `mnemonic_length_invalid`: 助记词长度非法（应为 BIP39 合法长度）。
- `mnemonic_word_invalid`: 助记词单词非法。
- `invalid_mnemonic_path`: 派生路径非法。
- `unsupported_chain`: 链类型未支持。

## Exchange Wallet Backend 集成建议
1. 先单链上线（ETH 或 BTC），跑通充值/归集/提现。
2. 统一地址管理与签名审计日志（不含私钥明文）。
3. 分层隔离：API 层、业务层、签名层。
4. 多链按插件方式逐步启用，避免一次性复杂度爆炸。

## 开发与测试
```bash
./gradlew test
./gradlew build
```

## CI / 发布说明
- CI 覆盖 Java 8/11/17。
- JitPack 依赖建议固定版本，不要使用动态版本。
