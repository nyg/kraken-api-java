package dev.andstuff.kraken.api.endpoint.funding.params;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code WalletTransfer}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class WalletTransferParams extends PostParams {

    /**
     * Asset to transfer (asset ID or {@code altname}).
     */
    @NonNull
    private final String asset;

    /**
     * Source wallet.
     */
    @NonNull
    private final SourceWallet sourceWallet;

    /**
     * Destination wallet.
     */
    @NonNull
    private final DestinationWallet destinationWallet;

    /**
     * Amount to transfer.
     */
    @NonNull
    private final BigDecimal amount;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "from", sourceWallet, SourceWallet::getValue);
        putIfNonNull(params, "to", destinationWallet, DestinationWallet::getValue);
        putIfNonNull(params, "amount", amount, BigDecimal::toPlainString);
        return params;
    }
}
