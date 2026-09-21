package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of Create Funding Address ({@code POST /funding/v1/addresses}), saving a crypto withdrawal address. Kraken verifies addresses created through the API automatically, without email confirmation; fiat addresses cannot be created this way.
 */
@Getter
@Builder(toBuilder = true)
public class CreateFundingAddressParams extends FundingBetaParams {

    /**
     * The method, network or network group the address can be used with.
     */
    @NonNull
    private final Scope scope;

    /**
     * The crypto address.
     */
    @NonNull
    private final String address;

    /**
     * The destination tag, on blockchains using one. Kraken accepts a tag or a memo, whichever the network uses.
     */
    private final String tag;

    /**
     * The memo, on blockchains using one. Kraken accepts a tag or a memo, whichever the network uses.
     */
    private final String memo;

    /**
     * The unique name of the address, with at least one non-whitespace character.
     */
    @NonNull
    private final String name;

    /**
     * The description of the address.
     */
    private final String description;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }

    @Override
    protected Map<String, Object> body() {
        Map<String, Object> crypto = new LinkedHashMap<>();
        crypto.put("address", address);
        putIfNonNull(crypto, "tag", tag);
        putIfNonNull(crypto, "memo", memo);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("scope", scope.json());
        body.put("address_details", Map.of("crypto", crypto));
        body.put("name", name);
        putIfNonNull(body, "description", description);
        return body;
    }
}
