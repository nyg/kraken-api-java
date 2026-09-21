package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of List Funding Claimed Addresses ({@code GET /funding/v2/deposit/addresses}); omitted options use Kraken's defaults. A cursor must be sent without the other filters.
 */
@Getter
@Builder(toBuilder = true)
public class FundingDepositAddressesParams extends FundingBetaParams {

    /**
     * The method or network to filter on. Filtering on a method also returns the addresses it shares with another method.
     */
    private final Scope scope;

    /**
     * The {@code nextCursor} of the previous page.
     */
    private final String cursor;

    /**
     * The maximum number of addresses returned, up to 500.
     */
    private final Integer limit;

    /**
     * The asset to filter on; its class is required, its name is optional.
     */
    private final Asset asset;

    /**
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    /**
     * Returns the query parameters sent to Kraken.
     *
     * @return the query parameters
     * @throws IllegalArgumentException if the scope is a network group
     */
    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        if (scope != null) {
            scope.requireMethodOrNetwork("List Funding Claimed Addresses");
            scope.putQuery(query, "scope");
        }
        putIfNonNull(query, "cursor", cursor, v -> v);
        putIfNonNull(query, "limit", limit, String::valueOf);
        if (asset != null) {
            asset.putQuery(query, "asset");
        }
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
