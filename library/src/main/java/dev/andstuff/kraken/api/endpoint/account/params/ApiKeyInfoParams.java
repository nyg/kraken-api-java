package dev.andstuff.kraken.api.endpoint.account.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of {@code GetApiKeyInfo}; omitted options use Kraken's defaults.
 */
@Getter
@Builder(toBuilder = true)
public class ApiKeyInfoParams extends PostParams {

    /**
     * The one-time password when API-key two-factor authentication is enabled.
     */
    private final String otp;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        putIfNonNull(params, "otp", otp);
        return params;
    }
}
