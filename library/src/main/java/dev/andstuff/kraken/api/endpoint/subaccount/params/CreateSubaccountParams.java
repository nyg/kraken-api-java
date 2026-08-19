package dev.andstuff.kraken.api.endpoint.subaccount.params;

import java.util.HashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.priv.PostParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The parameters of the {@code CreateSubaccount} endpoint.
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class CreateSubaccountParams extends PostParams {

    @NonNull
    private final String username;

    @NonNull
    private final String email;

    @Override
    protected Map<String, String> params() {
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);
        return params;
    }
}
