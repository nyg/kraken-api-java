package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * The parameters of Claim Funding Deposit Address ({@code PUT /funding/v1/deposit/address}).
 */
@Getter
@Builder(toBuilder = true)
public class ClaimFundingDepositAddressParams extends FundingBetaParams {

    /**
     * The deposit method to claim an address for. Methods sharing addresses with another method on the same network return the shared address.
     */
    @NonNull
    private final String methodId;

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
        body.put("method_id", methodId);
        return body;
    }
}
