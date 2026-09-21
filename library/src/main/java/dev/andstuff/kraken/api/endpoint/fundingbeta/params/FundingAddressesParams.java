package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import dev.andstuff.kraken.api.endpoint.fundingbeta.FundingBetaParams;
import lombok.Builder;
import lombok.Getter;

/**
 * The parameters of List Funding Addresses ({@code GET /funding/v1/addresses}); omitted options use Kraken's defaults. A cursor must be sent without the other filters.
 */
@Getter
@Builder(toBuilder = true)
public class FundingAddressesParams extends FundingBetaParams {

    /**
     * The scope to filter on. A method scope also returns the addresses saved for its network and network group, a network scope those saved for its network group.
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
     * The account ID; omit to use the account of the API key.
     */
    private final String accountId;

    @Override
    public Map<String, String> toMap() {
        Map<String, String> query = new LinkedHashMap<>();
        if (scope != null) {
            scope.putQuery(query, "scope");
        }
        putIfNonNull(query, "cursor", cursor, v -> v);
        putIfNonNull(query, "limit", limit, String::valueOf);
        putIfNonNull(query, "account_id", accountId, v -> v);
        return query;
    }
}
