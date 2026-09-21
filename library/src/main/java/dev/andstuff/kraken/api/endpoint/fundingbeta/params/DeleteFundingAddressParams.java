package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of Delete Funding Address ({@code DELETE /funding/v1/addresses/{id}}).
 */
@Getter
@Builder(toBuilder = true)
public class DeleteFundingAddressParams extends FundingBetaParams {

    /**
     * The identifier of the address to delete, sent in the path.
     */
    @NonNull
    private final String addressId;

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
}
