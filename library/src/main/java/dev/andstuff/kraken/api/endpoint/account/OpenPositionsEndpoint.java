package dev.andstuff.kraken.api.endpoint.account;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.Consolidation;
import dev.andstuff.kraken.api.endpoint.account.params.OpenPositionsParams;
import dev.andstuff.kraken.api.endpoint.account.response.OpenPosition;

/**
 * The private {@code OpenPositions} endpoint for open positions, by trade transaction ID. Positions consolidated by pair are returned by {@link ConsolidatedPositionsEndpoint}.
 */
public class OpenPositionsEndpoint extends WalletScopedEndpoint<Map<String, OpenPosition>> {

    /**
     * Creates the {@code OpenPositions} endpoint with default options.
     */
    public OpenPositionsEndpoint() {
        this(OpenPositionsParams.builder().build());
    }

    /**
     * Creates the {@code OpenPositions} endpoint.
     *
     * @param params the request parameters
     * @throws IllegalArgumentException if {@code consolidation} is {@code market}, whose response is read by {@link ConsolidatedPositionsEndpoint}
     */
    public OpenPositionsEndpoint(OpenPositionsParams params) {
        super("OpenPositions", params, params.getAccountId(), new TypeReference<>() {});
        if (params.getConsolidation() == Consolidation.MARKET) {
            throw new IllegalArgumentException("Use ConsolidatedPositionsEndpoint for positions consolidated by market");
        }
    }
}
