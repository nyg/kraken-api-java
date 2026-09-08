package dev.andstuff.kraken.api.endpoint.funding.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;

import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code WithdrawAddresses}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class WithdrawalAddressesParams extends PostParams {

    /**
     * Filter addresses for specific asset.
     */
    private final String asset;

    /**
     * Filter addresses for specific asset class.
     */
    private final AssetClass assetClass;

    /**
     * Filter addresses for specific method.
     */
    private final String method;

    /**
     * The withdrawal key name configured in the Kraken account.
     */
    private final String key;

    /**
     * Filter by verification status of the withdrawal address. Withdrawal addresses successfully completing email confirmation will have a verification status of true.
     */
    private final Boolean verified;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "asset", asset);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        putIfNonNull(params, "method", method);
        putIfNonNull(params, "key", key);
        putIfNonNull(params, "verified", verified);
        return params;
    }
}
