package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code ListWalletAccounts}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class WalletAccountsParams extends PostParams {

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        return params;
    }
}
