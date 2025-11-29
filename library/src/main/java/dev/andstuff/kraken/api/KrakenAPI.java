package dev.andstuff.kraken.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

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
import dev.andstuff.kraken.api.rest.DefaultKrakenRestRequester;
import dev.andstuff.kraken.api.rest.EpochBasedNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenCredentials;
import dev.andstuff.kraken.api.rest.KrakenNonceGenerator;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder(toBuilder = true)
public class KrakenAPI {

    private final KrakenCredentials credentials;
    private final KrakenNonceGenerator nonceGenerator;
    private final KrakenRestRequester restRequester;

    public KrakenAPI() {
        this(null, new DefaultKrakenRestRequester());
    }

    public KrakenAPI(String key, String secret) {
        this(new KrakenCredentials(key, secret));
    }

    public KrakenAPI(KrakenCredentials credentials) {
        this(credentials, new DefaultKrakenRestRequester());
    }

    public KrakenAPI(KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator) {
        this(credentials, nonceGenerator, new DefaultKrakenRestRequester());
    }

    public KrakenAPI(KrakenCredentials credentials, KrakenRestRequester restRequester) {
        this(credentials, new EpochBasedNonceGenerator(), restRequester);
    }

    public KrakenAPI(KrakenCredentials credentials, KrakenNonceGenerator nonceGenerator, KrakenRestRequester restRequester) {
        this.credentials = credentials;
        this.nonceGenerator = nonceGenerator;
        this.restRequester = restRequester;
    }

    /* Implemented public endpoints */

    public ServerTime serverTime() {
        return restRequester.execute(new ServerTimeEndpoint());
    }

    public SystemStatus systemStatus() {
        return restRequester.execute(new SystemStatusEndpoint());
    }

    public Map<String, AssetInfo> assetInfo(List<String> assets) {
        return restRequester.execute(new AssetInfoEndpoint(assets));
    }

    public Map<String, AssetInfo> assetInfo(List<String> assets, String assetClass) {
        return restRequester.execute(new AssetInfoEndpoint(assets, assetClass));
    }

    public AssetPairs assetPairs() {
        return restRequester.execute(new AssetPairEndpoint());
    }

    public AssetPairs assetPairs(List<String> pairs) {
        return restRequester.execute(new AssetPairEndpoint(pairs));
    }

    public AssetPairs assetPairs(List<String> pair, AssetPairParams.Info info) {
        return restRequester.execute(new AssetPairEndpoint(pair, info));
    }

    public Map<String, Ticker> ticker(List<String> pairs) {
        return restRequester.execute(new TickerEndpoint(pairs));
    }

    /* Implemented private endpoints */

    public LedgerInfo ledgerInfo(LedgerInfoParams params) {
        return executePrivate(new LedgerInfoEndpoint(params));
    }

    public Map<String, LedgerEntry> ledgerEntries(LedgerEntriesParams params) {
        return executePrivate(new LedgerEntriesEndpoint(params));
    }

    public ReportRequest requestReport(RequestReportParams params) {
        return executePrivate(new RequestReportEndpoint(params));
    }

    public List<Report> reportsStatuses(ReportType type) {
        return executePrivate(new ReportsStatusesEndpoint(ReportsStatusesParams.of(type)));
    }

    public List<LedgerEntry> reportData(String id) {
        return executePrivate(new ReportDataEndpoint(ReportDataParams.of(id)));
    }

    public boolean deleteReport(String id) {
        return executePrivate(new RemoveReportEndpoint(RemoveReportParams.of(id, RemovalType.DELETE))).wasDeleted();
    }

    public boolean cancelReport(String id) {
        return executePrivate(new RemoveReportEndpoint(RemoveReportParams.of(id, RemovalType.CANCEL))).wasCanceled();
    }

    private <T> T executePrivate(PrivateEndpoint<T> endpoint) {
        return restRequester.execute(endpoint, credentials, nonceGenerator);
    }

    /* Query unimplemented endpoints */

    public JsonNode query(Public endpoint) {
        return restRequester.execute(new JsonPublicEndpoint(endpoint.getPath()));
    }

    public JsonNode query(Public endpoint, Map<String, String> queryParams) {
        return restRequester.execute(new JsonPublicEndpoint(endpoint.getPath(), queryParams));
    }

    public JsonNode queryPublic(String path) {
        return restRequester.execute(new JsonPublicEndpoint(path));
    }

    public JsonNode queryPublic(String path, Map<String, String> queryParams) {
        return restRequester.execute(new JsonPublicEndpoint(path, queryParams));
    }

    public JsonNode query(Private endpoint) {
        return executePrivate(new JsonPrivateEndpoint(endpoint.getPath()));
    }

    public JsonNode query(Private endpoint, Map<String, String> params) {
        return executePrivate(new JsonPrivateEndpoint(endpoint.getPath(), params));
    }

    public JsonNode queryPrivate(String path) {
        return executePrivate(new JsonPrivateEndpoint(path));
    }

    public JsonNode queryPrivate(String path, Map<String, String> params) {
        return executePrivate(new JsonPrivateEndpoint(path, params));
    }

    /* All endpoints */

    @Getter
    @RequiredArgsConstructor
    public enum Public {
        ASSETS("Assets"),
        ASSET_PAIRS("AssetPairs"),
        DEPTH("Depth"),
        OHLC("OHLC"),
        SPREAD("Spread"),
        SYSTEM_STATUS("SystemStatus"),
        TICKER("Ticker"),
        TIME("Time"),
        TRADES("Trades");

        private final String path;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Private {
        ACCOUNT_TRANSFER("AccountTransfer"),
        ADD_EXPORT("AddExport"),
        ADD_ORDER("AddOrder"),
        ADD_ORDER_BATCH("AddOrderBatch"),
        BALANCE("Balance"),
        BALANCE_EX("BalanceEx"),
        CANCEL_ALL("CancelAll"),
        CANCEL_ALL_ORDERS_AFTER("CancelAllOrdersAfter"),
        CANCEL_ORDER("CancelOrder"),
        CANCEL_ORDER_BATCH("CancelOrderBatch"),
        CLOSED_ORDERS("ClosedOrders"),
        CREATE_SUB_ACCOUNT("CreateSubaccount"),
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
        GET_WEBSOCKETS_TOKEN("GetWebSocketsToken"),
        LEDGERS("Ledgers"),
        OPEN_ORDERS("OpenOrders"),
        OPEN_POSITIONS("OpenPositions"),
        QUERY_LEDGERS("QueryLedgers"),
        QUERY_ORDERS("QueryOrders"),
        QUERY_TRADES("QueryTrades"),
        REMOVE_EXPORT("RemoveExport"),
        RETRIEVE_EXPORT("RetrieveExport"),
        STAKE("Stake"),
        STAKING_ASSETS("Staking/Assets"),
        STAKING_PENDING("Staking/Pending"),
        STAKING_TRANSACTIONS("Staking/Transactions"),
        TRADES_HISTORY("TradesHistory"),
        TRADE_BALANCE("TradeBalance"),
        TRADE_VOLUME("TradeVolume"),
        UNSTAKE("Unstake"),
        WALLET_TRANSFER("WalletTransfer"),
        WITHDRAW("Withdraw"),
        WITHDRAW_ADDRESSES("WithdrawAddresses"),
        WITHDRAW_CANCEL("WithdrawCancel"),
        WITHDRAW_INFO("WithdrawInfo"),
        WITHDRAW_METHODS("WithdrawMethods"),
        WITHDRAW_STATUS("WithdrawStatus");

        public final String path;
    }
}
