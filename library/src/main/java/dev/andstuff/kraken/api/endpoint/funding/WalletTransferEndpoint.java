package dev.andstuff.kraken.api.endpoint.funding;

import java.math.BigDecimal;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.funding.params.DestinationWallet;
import dev.andstuff.kraken.api.endpoint.funding.params.SourceWallet;
import dev.andstuff.kraken.api.endpoint.funding.params.WalletTransferParams;
import dev.andstuff.kraken.api.endpoint.funding.response.FundingReference;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code WalletTransfer} endpoint for wallet transfer.
 */
public class WalletTransferEndpoint extends PrivateEndpoint<FundingReference> {

    /**
     * Creates the {@code WalletTransfer} endpoint using the required parameters.
     *
     * @param asset the asset sent to Kraken
     * @param sourceWallet the sourceWallet sent to Kraken
     * @param destinationWallet the destinationWallet sent to Kraken
     * @param amount the amount sent to Kraken
     */
    public WalletTransferEndpoint(String asset, SourceWallet sourceWallet, DestinationWallet destinationWallet, BigDecimal amount) {
        this(WalletTransferParams.builder().asset(asset).sourceWallet(sourceWallet).destinationWallet(destinationWallet).amount(amount).build());
    }

    /**
     * Creates the {@code WalletTransfer} endpoint.
     *
     * @param params the request parameters
     */
    public WalletTransferEndpoint(WalletTransferParams params) {
        super("WalletTransfer", params, new TypeReference<>() {});
    }
}
