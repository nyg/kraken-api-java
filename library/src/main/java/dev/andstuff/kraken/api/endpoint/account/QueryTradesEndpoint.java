package dev.andstuff.kraken.api.endpoint.account;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.account.params.QueryTradesParams;
import dev.andstuff.kraken.api.endpoint.account.response.AccountTrade;

/**
 * The private {@code QueryTrades} endpoint for query trades.
 */
public class QueryTradesEndpoint extends WalletScopedEndpoint<Map<String, AccountTrade>> {

    /**
     * Creates the {@code QueryTrades} endpoint for the selected transaction IDs.
     *
     * @param transactionIds the transaction IDs to query
     */
    public QueryTradesEndpoint(List<String> transactionIds) {
        this(QueryTradesParams.builder().transactionIds(transactionIds).build());
    }

    /**
     * Creates the {@code QueryTrades} endpoint.
     *
     * @param params the request parameters
     */
    public QueryTradesEndpoint(QueryTradesParams params) {
        super("QueryTrades", params, params.getAccountId(), new TypeReference<>() {});
    }
}
