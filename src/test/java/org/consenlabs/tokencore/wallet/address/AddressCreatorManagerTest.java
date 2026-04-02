package org.consenlabs.tokencore.wallet.address;

import org.consenlabs.tokencore.wallet.model.ChainType;
import org.consenlabs.tokencore.wallet.model.Metadata;
import org.consenlabs.tokencore.wallet.model.TokenException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressCreatorManagerTest {

    @Test
    void getInstance_ethereum_shouldReturnEthereumCreator() {
        AddressCreator creator = AddressCreatorManager.getInstance(
                ChainType.ETHEREUM, true, null);
        assertInstanceOf(EthereumAddressCreator.class, creator);
    }

    @Test
    void getInstance_tron_shouldReturnTronCreator() {
        AddressCreator creator = AddressCreatorManager.getInstance(
                ChainType.TRON, true, null);
        assertInstanceOf(TronAddressCreator.class, creator);
    }

    @Test
    void getInstance_filecoin_shouldReturnFilecoinCreator() {
        AddressCreator creator = AddressCreatorManager.getInstance(
                ChainType.FILECOIN, true, null);
        assertInstanceOf(FilecoinAddressCreator.class, creator);
    }

    @Test
    void getInstance_bitcoin_segwit_shouldReturnSegWitCreator() {
        AddressCreator creator = AddressCreatorManager.getInstance(
                ChainType.BITCOIN, true, Metadata.P2WPKH);
        assertInstanceOf(SegWitBitcoinAddressCreator.class, creator);
    }

    @Test
    void getInstance_bitcoin_legacy_shouldReturnBitcoinCreator() {
        AddressCreator creator = AddressCreatorManager.getInstance(
                ChainType.BITCOIN, true, Metadata.NONE);
        assertInstanceOf(BitcoinAddressCreator.class, creator);
    }

    @Test
    void getInstance_litecoin_shouldReturnBitcoinCreator() {
        AddressCreator creator = AddressCreatorManager.getInstance(
                ChainType.LITECOIN, true, null);
        assertInstanceOf(BitcoinAddressCreator.class, creator);
    }

    @Test
    void getInstance_unsupportedType_shouldThrow() {
        assertThrows(TokenException.class, () ->
                AddressCreatorManager.getInstance("UNSUPPORTED", true, null));
    }
}
