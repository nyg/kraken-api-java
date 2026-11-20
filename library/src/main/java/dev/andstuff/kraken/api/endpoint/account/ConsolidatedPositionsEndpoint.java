package dev.andstuff.kraken.api.endpoint.account;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.Consolidation;
import dev.andstuff.kraken.api.endpoint.account.params.OpenPositionsParams;
import dev.andstuff.kraken.api.endpoint.account.response.ConsolidatedPosition;

/**
 * The private {@code OpenPositions} endpoint with {@code consolidation=market}, returning the open margin positions consolidated by pair.
 */
public class ConsolidatedPositionsEndpoint extends WalletScopedEndpoint<List<ConsolidatedPosition>> {

    /**
     * Creates the consolidated {@code OpenPositions} endpoint with default options.
     */
    public ConsolidatedPositionsEndpoint() {
        this(OpenPositionsParams.builder().build());
    }

    /**
     * Creates the consolidated {@code OpenPositions} endpoint, sending {@code consolidation=market} whatever the given consolidation.
     *
     * @param params the request parameters
     */
    public ConsolidatedPositionsEndpoint(OpenPositionsParams params) {
        super("OpenPositions", params.toBuilder().consolidation(Consolidation.MARKET).build(), params.getAccountId(), new TypeReference<>() {});
    }
}
