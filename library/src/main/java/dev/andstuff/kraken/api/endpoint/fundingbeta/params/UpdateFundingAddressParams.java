package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of Update Funding Address ({@code PUT /funding/v1/addresses/{id}}), renaming or describing a saved withdrawal address. Omitted fields are left unchanged.
 */
@Getter
@Builder(toBuilder = true)
public class UpdateFundingAddressParams extends FundingBetaParams {

    /**
     * The identifier of the address to update, sent in the path.
     */
    @NonNull
    private final String addressId;

    /**
     * The new unique name of the address, with at least one non-whitespace character.
     */
    private final String name;

    /**
     * The new description of the address.
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
        Map<String, Object> body = new LinkedHashMap<>();
        putIfNonNull(body, "name", name);
        putIfNonNull(body, "description", description);
        return body;
    }
}
