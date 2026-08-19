package dev.andstuff.kraken.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.account.LedgerEntriesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.LedgerInfoEndpoint;
import dev.andstuff.kraken.api.endpoint.account.RemoveReportEndpoint;
import dev.andstuff.kraken.api.endpoint.account.ReportDataEndpoint;
import dev.andstuff.kraken.api.endpoint.account.ReportsStatusesEndpoint;
import dev.andstuff.kraken.api.endpoint.account.RequestReportEndpoint;
import dev.andstuff.kraken.api.endpoint.account.params.LedgerEntriesParams;
import dev.andstuff.kraken.api.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.endpoint.account.params.RemovalType;
import dev.andstuff.kraken.api.endpoint.account.params.RemoveReportParams;
import dev.andstuff.kraken.api.endpoint.account.params.ReportDataParams;
import dev.andstuff.kraken.api.endpoint.account.params.ReportType;
import dev.andstuff.kraken.api.endpoint.account.params.ReportsStatusesParams;
import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.api.endpoint.account.response.Report;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRequest;
import dev.andstuff.kraken.api.endpoint.market.AssetInfoEndpoint;
import dev.andstuff.kraken.api.endpoint.market.AssetPairEndpoint;
import dev.andstuff.kraken.api.endpoint.market.ServerTimeEndpoint;
import dev.andstuff.kraken.api.endpoint.market.SystemStatusEndpoint;
import dev.andstuff.kraken.api.endpoint.market.TickerEndpoint;
import dev.andstuff.kraken.api.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPairs;
import dev.andstuff.kraken.api.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.endpoint.market.response.SystemStatus;
import dev.andstuff.kraken.api.endpoint.market.response.Ticker;
import dev.andstuff.kraken.api.endpoint.priv.JsonPrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;
import dev.andstuff.kraken.api.endpoint.pub.JsonPublicEndpoint;
import dev.andstuff.kraken.api.endpoint.pub.PublicEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.AccountTransferEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.CreateSubaccountEndpoint;
import dev.andstuff.kraken.api.endpoint.subaccount.params.AccountTransferParams;
import dev.andstuff.kraken.api.endpoint.subaccount.params.CreateSubaccountParams;
import dev.andstuff.kraken.api.endpoint.subaccount.response.AccountTransfer;
import dev.andstuff.kraken.api.endpoint.transparency.PostTradeEndpoint;
import dev.andstuff.kraken.api.endpoint.transparency.PreTradeEndpoint;
import dev.andstuff.kraken.api.endpoint.transparency.params.PostTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.params.PreTradeParams;
import dev.andstuff.kraken.api.endpoint.transparency.response.PostTrade;
import dev.andstuff.kraken.api.endpoint.transparency.response.PreTrade;
import dev.andstuff.kraken.api.rest.DefaultKrakenRestRequester;
import dev.andstuff.kraken.api.rest.EpochBasedNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Entry point of the library, giving access to the Kraken REST API.
 *
 * <p>Endpoints can be queried in four ways, from the most to the least typed:
 * <ol>
 *     <li>through {@link #query(PublicEndpoint)} or {@link #query(PrivateEndpoint)}, taking an endpoint written outside the library and returning the type that endpoint declares;</li>
 *     <li>through a typed method, for the endpoints implemented by the library, e.g. {@link #assetInfo(List)};</li>
 *     <li>through a generic {@code query} method, taking a {@link Public} or {@link Private} enum value and returning a {@link JsonNode};</li>
 *     <li>through a raw {@code queryPublic} or {@code queryPrivate} method, taking the endpoint path as a string, for endpoints Kraken added but the library doesn't know about yet.</li>
 * </ol>
 *
 * <p>Credentials are only required for private endpoints. An instance keeps no per-request state, so it can be shared as long as the configured {@link KrakenRestRequester} and {@link KrakenNonceGenerator} can be.
 *
 * @see <a href="https://docs.kraken.com/rest/">Kraken REST API documentation</a>
 */
@Builder(toBuilder = true)
public class KrakenAPI {

    private final KrakenCredentials credentials;
    private final KrakenNonceGenerator nonceGenerator;
    private final KrakenRestRequester restRequester;

    /**
     * Creates an instance without credentials, using the default REST requester. Only public endpoints can be queried.
     */
    public KrakenAPI() {
        this(null, new DefaultKrakenRestRequester());
    }

    /**
     * Creates an instance using the default REST requester and nonce generator.
     *
     * @param key the Kraken API key
     * @param secret the Kraken API secret, Base64 encoded, as given by Kraken
     */
    public KrakenAPI(String key, String secret) {
        this(new KrakenCredentials(key, secret));
    }

    /**
     * Creates an instance using the default REST requester and nonce generator.
     *
     * @param credentials the credentials used to sign private endpoint requests
     */
    public KrakenAPI(KrakenCredentials credentials) {
        this(credentials, new DefaultKrakenRestRequester());
    }

    /**
     * Creates an instance using the default REST requester.
     *
     * @param credentials the credentials used to sign private endpoint requests
     * @param nonceGenerator the generator providing the nonce of each private endpoint request
     */
    public KrakenAPI(KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator) {
        this(credentials, nonceGenerator, new DefaultKrakenRestRequester());
    }

    /**
     * Creates an instance using the default nonce generator.
     *
     * @param credentials the credentials used to sign private endpoint requests
     * @param restRequester the requester performing the HTTP calls
     */
    public KrakenAPI(KrakenCredentials credentials, KrakenRestRequester restRequester) {
        this(credentials, new EpochBasedNonceGenerator(), restRequester);
    }

    /**
     * Creates a fully configured instance.
     *
     * @param credentials the credentials used to sign private endpoint requests
     * @param nonceGenerator the generator providing the nonce of each private endpoint request
     * @param restRequester the requester performing the HTTP calls
     */
    public KrakenAPI(KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator, KrakenRestRequester restRequester) {
        this.credentials = credentials;
        this.nonceGenerator = nonceGenerator;
        this.restRequester = restRequester;
    }

    /* Implemented public endpoints */

    /**
     * Queries the {@code Time} endpoint, returning Kraken's server time.
     *
     * @return the server time
     * @throws KrakenException if Kraken returns an error
     */
    public ServerTime serverTime() {
        return query(new ServerTimeEndpoint());
    }

    /**
     * Queries the {@code SystemStatus} endpoint, returning the current status of the Kraken trading system.
     *
     * @return the system status
     * @throws KrakenException if Kraken returns an error
     */
    public SystemStatus systemStatus() {
        return query(new SystemStatusEndpoint());
    }

    /**
     * Queries the {@code Assets} endpoint for the {@code currency} asset class.
     *
     * @param assets the assets to retrieve information for, e.g. {@code ["BTC", "ETH"]}
     * @return the asset information, by asset name
     * @throws KrakenException if Kraken returns an error
     */
    public Map<String, AssetInfo> assetInfo(List<String> assets) {
        return query(new AssetInfoEndpoint(assets));
    }

    /**
     * Queries the {@code Assets} endpoint.
     *
     * @param assets the assets to retrieve information for, e.g. {@code ["BTC", "ETH"]}
     * @param assetClass the asset class to filter on, e.g. {@code currency}
     * @return the asset information, by asset name
     * @throws KrakenException if Kraken returns an error
     */
    public Map<String, AssetInfo> assetInfo(List<String> assets, String assetClass) {
        return query(new AssetInfoEndpoint(assets, assetClass));
    }

    /**
     * Queries the {@code AssetPairs} endpoint for all tradable asset pairs.
     *
     * @return the asset pairs
     * @throws KrakenException if Kraken returns an error
     */
    public AssetPairs assetPairs() {
        return query(new AssetPairEndpoint());
    }

    /**
     * Queries the {@code AssetPairs} endpoint.
     *
     * @param pairs the asset pairs to retrieve, e.g. {@code ["ETH/BTC", "ETH/USD"]}
     * @return the asset pairs
     * @throws KrakenException if Kraken returns an error
     */
    public AssetPairs assetPairs(List<String> pairs) {
        return query(new AssetPairEndpoint(pairs));
    }

    /**
     * Queries the {@code AssetPairs} endpoint, restricting the information returned for each pair.
     *
     * @param pair the asset pairs to retrieve, e.g. {@code ["ETH/BTC", "ETH/USD"]}
     * @param info the subset of information to return
     * @return the asset pairs
     * @throws KrakenException if Kraken returns an error
     */
    public AssetPairs assetPairs(List<String> pair, AssetPairParams.Info info) {
        return query(new AssetPairEndpoint(pair, info));
    }

    /**
     * Queries the {@code Ticker} endpoint.
     *
     * @param pairs the asset pairs to retrieve the ticker of, e.g. {@code ["XBTUSD"]}
     * @return the ticker information, by asset pair name
     * @throws KrakenException if Kraken returns an error
     */
    public Map<String, Ticker> ticker(List<String> pairs) {
        return query(new TickerEndpoint(pairs));
    }

    /**
     * Queries the {@code PreTrade} endpoint, returning the aggregated order book of a currency pair, with at most ten price levels on each side.
     *
     * @param symbol the currency pair, in the {@code BASE/QUOTE} display format, e.g. {@code BTC/USD}
     * @return the aggregated order book
     * @throws KrakenException if Kraken returns an error
     */
    public PreTrade preTrade(String symbol) {
        return query(new PreTradeEndpoint(PreTradeParams.of(symbol)));
    }

    /**
     * Queries the {@code PostTrade} endpoint, returning the last 1000 trades executed on a currency pair.
     *
     * @param symbol the currency pair, in the {@code BASE/QUOTE} display format, e.g. {@code BTC/USD}
     * @return the executed trades
     * @throws KrakenException if Kraken returns an error
     */
    public PostTrade postTrade(String symbol) {
        return query(new PostTradeEndpoint(PostTradeParams.builder().symbol(symbol).build()));
    }

    /**
     * Queries the {@code PostTrade} endpoint, returning the trades executed on a currency pair over the given period. Trades are returned in ascending time order and at most 1000 at a time: {@link PostTrade#lastTimestamp()} gives the timestamp to use as the next {@code fromTimestamp}.
     *
     * @param params the currency pair and the period and count restricting the trades returned
     * @return the executed trades
     * @throws KrakenException if Kraken returns an error
     */
    public PostTrade postTrade(PostTradeParams params) {
        return query(new PostTradeEndpoint(params));
    }

    /* Implemented private endpoints */

    /**
     * Queries the private {@code Ledgers} endpoint, returning at most 50 ledger entries per call.
     *
     * @param params the filtering and pagination parameters
     * @return the matching ledger entries and their total count
     * @throws KrakenException if Kraken returns an error
     */
    public LedgerInfo ledgerInfo(LedgerInfoParams params) {
        return query(new LedgerInfoEndpoint(params));
    }

    /**
     * Queries the private {@code QueryLedgers} endpoint, returning specific ledger entries by identifier.
     *
     * @param params the ledger entry identifiers to retrieve
     * @return the ledger entries, by identifier
     * @throws KrakenException if Kraken returns an error
     */
    public Map<String, LedgerEntry> ledgerEntries(LedgerEntriesParams params) {
        return query(new LedgerEntriesEndpoint(params));
    }

    /**
     * Queries the private {@code AddExport} endpoint, asking Kraken to generate a report. The report is generated asynchronously: use {@link #reportsStatuses(ReportType)} to know when it is ready and {@link #reportData(String)} to download it.
     *
     * @param params the type, format and period of the report
     * @return the identifier of the requested report
     * @throws KrakenException if Kraken returns an error
     */
    public ReportRequest requestReport(RequestReportParams params) {
        return query(new RequestReportEndpoint(params));
    }

    /**
     * Queries the private {@code ExportStatus} endpoint, returning the status of the previously requested reports.
     *
     * @param type the type of report to list
     * @return the reports and their status
     * @throws KrakenException if Kraken returns an error
     */
    public List<Report> reportsStatuses(ReportType type) {
        return query(new ReportsStatusesEndpoint(ReportsStatusesParams.of(type)));
    }

    /**
     * Queries the private {@code RetrieveExport} endpoint, downloading a processed report and parsing the CSV file it contains.
     *
     * @param id the identifier of the report, as returned by {@link #requestReport(RequestReportParams)}
     * @return the ledger entries contained in the report
     * @throws KrakenException if Kraken returns an error
     */
    public List<LedgerEntry> reportData(String id) {
        return query(new ReportDataEndpoint(ReportDataParams.of(id)));
    }

    /**
     * Queries the private {@code RemoveExport} endpoint to delete a processed report.
     *
     * @param id the identifier of the report to delete
     * @return whether the report was deleted
     * @throws KrakenException if Kraken returns an error
     */
    public boolean deleteReport(String id) {
        return query(new RemoveReportEndpoint(RemoveReportParams.of(id, RemovalType.DELETE))).wasDeleted();
    }

    /**
     * Queries the private {@code RemoveExport} endpoint to cancel a report that is still being generated.
     *
     * @param id the identifier of the report to cancel
     * @return whether the report was canceled
     * @throws KrakenException if Kraken returns an error
     */
    public boolean cancelReport(String id) {
        return query(new RemoveReportEndpoint(RemoveReportParams.of(id, RemovalType.CANCEL))).wasCanceled();
    }

    /**
     * Queries the private {@code CreateSubaccount} endpoint, creating a trading subaccount. It must be called with an API key of the master account, having the withdraw funds permission.
     *
     * @param params the username and email address of the subaccount
     * @return whether the subaccount was created
     * @throws KrakenException if Kraken returns an error
     */
    public boolean createSubaccount(CreateSubaccountParams params) {
        return query(new CreateSubaccountEndpoint(params));
    }

    /**
     * Queries the private {@code AccountTransfer} endpoint, transferring funds between the master account and its subaccounts. It must be called with an API key of the master account, having the withdraw funds permission.
     *
     * @param params the asset, amount and accounts of the transfer
     * @return the identifier and status of the transfer
     * @throws KrakenException if Kraken returns an error
     */
    public AccountTransfer accountTransfer(AccountTransferParams params) {
        return query(new AccountTransferEndpoint(params));
    }

    /* Query unimplemented endpoints */

    /**
     * Queries a public endpoint the library doesn't implement, described by a {@link PublicEndpoint} written outside the library.
     *
     * @param <T> the type the response is deserialized into
     * @param endpoint the public endpoint to query
     * @return the deserialized {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public <T> T query(PublicEndpoint<T> endpoint) {
        return restRequester.execute(endpoint);
    }

    /**
     * Queries a private endpoint the library doesn't implement, described by a {@link PrivateEndpoint} written outside the library. The request is signed with the credentials of this instance.
     *
     * @param <T> the type the response is deserialized into
     * @param endpoint the private endpoint to query
     * @return the deserialized {@code result} field of the Kraken response
     * @throws IllegalStateException if this instance was built without credentials
     * @throws KrakenException if Kraken returns an error
     */
    public <T> T query(PrivateEndpoint<T> endpoint) {
        if (credentials == null) {
            throw new IllegalStateException("Private endpoint %s requires credentials, build KrakenAPI with a KrakenCredentials instance".formatted(endpoint.getPath()));
        }

        return restRequester.execute(endpoint, credentials, nonceGenerator);
    }

    /**
     * Queries a public endpoint the library doesn't implement, without parameters.
     *
     * @param endpoint the public endpoint to query
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode query(Public endpoint) {
        return query(new JsonPublicEndpoint(endpoint.getPath()));
    }

    /**
     * Queries a public endpoint the library doesn't implement.
     *
     * @param endpoint the public endpoint to query
     * @param queryParams the URL query parameters, as expected by Kraken
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode query(Public endpoint, Map<String, String> queryParams) {
        return query(new JsonPublicEndpoint(endpoint.getPath(), queryParams));
    }

    /**
     * Queries a public endpoint by path, without parameters, for endpoints missing from {@link Public}.
     *
     * @param path the endpoint path, e.g. {@code Trades} for {@code /0/public/Trades}
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode queryPublic(String path) {
        return query(new JsonPublicEndpoint(path));
    }

    /**
     * Queries a public endpoint by path, for endpoints missing from {@link Public}.
     *
     * @param path the endpoint path, e.g. {@code Trades} for {@code /0/public/Trades}
     * @param queryParams the URL query parameters, as expected by Kraken
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode queryPublic(String path, Map<String, String> queryParams) {
        return query(new JsonPublicEndpoint(path, queryParams));
    }

    /**
     * Queries a private endpoint the library doesn't implement, without parameters.
     *
     * @param endpoint the private endpoint to query
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode query(Private endpoint) {
        return query(new JsonPrivateEndpoint(endpoint.getPath()));
    }

    /**
     * Queries a private endpoint the library doesn't implement.
     *
     * @param endpoint the private endpoint to query
     * @param params the POST parameters, as expected by Kraken, the nonce being added by the library
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode query(Private endpoint, Map<String, String> params) {
        return query(new JsonPrivateEndpoint(endpoint.getPath(), params));
    }

    /**
     * Queries a private endpoint by path, without parameters, for endpoints missing from {@link Private}.
     *
     * @param path the endpoint path, e.g. {@code Balance} for {@code /0/private/Balance}
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode queryPrivate(String path) {
        return query(new JsonPrivateEndpoint(path));
    }

    /**
     * Queries a private endpoint by path, for endpoints missing from {@link Private}.
     *
     * @param path the endpoint path, e.g. {@code Balance} for {@code /0/private/Balance}
     * @param params the POST parameters, as expected by Kraken, the nonce being added by the library
     * @return the raw {@code result} field of the Kraken response
     * @throws KrakenException if Kraken returns an error
     */
    public JsonNode queryPrivate(String path, Map<String, String> params) {
        return query(new JsonPrivateEndpoint(path, params));
    }

    /* All endpoints */

    /**
     * The public endpoints of the Kraken REST API, to be used with {@link KrakenAPI#query(Public)}.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Public {
        ASSETS("Assets"),
        ASSET_PAIRS("AssetPairs"),
        DEPTH("Depth"),
        GROUPED_BOOK("GroupedBook"),
        OHLC("OHLC"),
        POST_TRADE("PostTrade"),
        PRE_TRADE("PreTrade"),
        SPREAD("Spread"),
        SYSTEM_STATUS("SystemStatus"),
        TICKER("Ticker"),
        TIME("Time"),
        TRADES("Trades");

        private final String path;
    }

    /**
     * The private endpoints of the Kraken REST API, to be used with {@link KrakenAPI#query(Private)}.
     */
    @Getter
    @RequiredArgsConstructor
    public enum Private {
        ACCOUNT_TRANSFER("AccountTransfer"),
        ADD_EXPORT("AddExport"),
        ADD_ORDER("AddOrder"),
        ADD_ORDER_BATCH("AddOrderBatch"),
        AMEND_ORDER("AmendOrder"),
        BALANCE("Balance"),
        BALANCE_EX("BalanceEx"),
        CANCEL_ALL("CancelAll"),
        CANCEL_ALL_ORDERS_AFTER("CancelAllOrdersAfter"),
        CANCEL_ORDER("CancelOrder"),
        CANCEL_ORDER_BATCH("CancelOrderBatch"),
        CLOSED_ORDERS("ClosedOrders"),
        CREATE_SUB_ACCOUNT("CreateSubaccount"),
        CREDIT_LINES("CreditLines"),
        DEPOSIT_ADDRESSES("DepositAddresses"),
        DEPOSIT_METHODS("DepositMethods"),
        DEPOSIT_STATUS("DepositStatus"),
        EARN_ALLOCATE("Earn/Allocate"),
        EARN_ALLOCATE_STATUS("Earn/AllocateStatus"),
        EARN_ALLOCATIONS("Earn/Allocations"),
        EARN_DEALLOCATE("Earn/Deallocate"),
        EARN_DEALLOCATE_STATUS("Earn/DeallocateStatus"),
        EARN_STRATEGIES("Earn/Strategies"),
        EDIT_ORDER("EditOrder"),
        EXPORT_STATUS("ExportStatus"),
        GET_API_KEY_INFO("GetApiKeyInfo"),
        GET_WEBSOCKETS_TOKEN("GetWebSocketsToken"),
        LEDGERS("Ledgers"),
        LEVEL3("Level3"),
        OPEN_ORDERS("OpenOrders"),
        OPEN_POSITIONS("OpenPositions"),
        ORDER_AMENDS("OrderAmends"),
        QUERY_LEDGERS("QueryLedgers"),
        QUERY_ORDERS("QueryOrders"),
        QUERY_TRADES("QueryTrades"),
        REMOVE_EXPORT("RemoveExport"),
        RETRIEVE_EXPORT("RetrieveExport"),
        TRADES_HISTORY("TradesHistory"),
        TRADE_BALANCE("TradeBalance"),
        TRADE_VOLUME("TradeVolume"),
        WALLET_TRANSFER("WalletTransfer"),
        WITHDRAW("Withdraw"),
        WITHDRAW_ADDRESSES("WithdrawAddresses"),
        WITHDRAW_CANCEL("WithdrawCancel"),
        WITHDRAW_INFO("WithdrawInfo"),
        WITHDRAW_METHODS("WithdrawMethods"),
        WITHDRAW_STATUS("WithdrawStatus");

        private final String path;
    }
}
