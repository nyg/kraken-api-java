package dev.andstuff.kraken.api.endpoint.funding.params;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of {@code DepositAddresses}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class DepositAddressesParams extends PostParams {

    /**
     * Asset being deposited.
     */
    @NonNull
    private final String asset;

    /**
     * Asset class being deposited.
     */
    private final AssetClass assetClass;

    /**
     * Name of the deposit method.
     */
    @NonNull
    private final String method;

    /**
     * Whether or not to generate a new address.
     */
    private final Boolean generateNew;

    /**
     * Amount you wish to deposit (only required for {@code method=Bitcoin Lightning}).
     */
    private final BigDecimal amount;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("asset", asset);
        putIfNonNull(params, "aclass", assetClass, AssetClass::getValue);
        params.put("method", method);
        putIfNonNull(params, "new", generateNew);
        putIfNonNull(params, "amount", amount, BigDecimal::toPlainString);
        return params;
    }
}
