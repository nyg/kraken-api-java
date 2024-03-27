package dev.andstuff.kraken.api;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import dev.andstuff.kraken.api.model.KrakenCredentials;
import dev.andstuff.kraken.api.model.endpoint.account.LedgerEntriesEndpoint;
import dev.andstuff.kraken.api.model.endpoint.account.LedgerInfoEndpoint;
import dev.andstuff.kraken.api.model.endpoint.account.params.LedgerEntriesParams;
import dev.andstuff.kraken.api.model.endpoint.account.params.LedgerInfoParams;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerEntry;
import dev.andstuff.kraken.api.model.endpoint.account.response.LedgerInfo;
import dev.andstuff.kraken.api.model.endpoint.market.AssetInfoEndpoint;
import dev.andstuff.kraken.api.model.endpoint.market.AssetPairEndpoint;
import dev.andstuff.kraken.api.model.endpoint.market.ServerTimeEndpoint;
import dev.andstuff.kraken.api.model.endpoint.market.SystemStatusEndpoint;
import dev.andstuff.kraken.api.model.endpoint.market.TickerEndpoint;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetInfo;
import dev.andstuff.kraken.api.model.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.model.endpoint.market.response.ServerTime;
import dev.andstuff.kraken.api.model.endpoint.market.response.SystemStatus;
import dev.andstuff.kraken.api.model.endpoint.market.response.Ticker;
import dev.andstuff.kraken.api.model.endpoint.priv.JsonPrivateEndpoint;
import dev.andstuff.kraken.api.model.endpoint.pub.JsonPublicEndpoint;
import dev.andstuff.kraken.api.rest.DefaultKrakenRestRequester;
import dev.andstuff.kraken.api.rest.KrakenRestRequester;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class KrakenAPI {

    private final KrakenRestRequester restRequester;

    public KrakenAPI() {
        this(new DefaultKrakenRestRequester());
    }

    public KrakenAPI(KrakenCredentials credentials) {
        this(new DefaultKrakenRestRequester(credentials));
    }

    public KrakenAPI(String key, String secret) {
        this(new DefaultKrakenRestRequester(key, secret));
    }

    public KrakenAPI(KrakenRestRequester restRequester) {
        this.restRequester = restRequester;
    }

    /* Implemented public endpoints */

    public ServerTime serverTime() {
        return restRequester.execute(new ServerTimeEndpoint());
    }

    public SystemStatus systemStatus() {
        return restRequester.execute(new SystemStatusEndpoint());
    }

    // TODO maybe return type Assets that can return both map and list
    public Map<String, AssetInfo> assetInfo(List<String> assets) {
        return restRequester.execute(new AssetInfoEndpoint(assets));
    }

    public Map<String, AssetInfo> assetInfo(List<String> assets, String assetClass) {
        return restRequester.execute(new AssetInfoEndpoint(assets, assetClass));
    }

    public Map<String, AssetPair> assetPairs(List<String> pairs) {
        return restRequester.execute(new AssetPairEndpoint(pairs));
    }

    public Map<String, AssetPair> assetPairs(List<String> pair, AssetPair.Info info) {
        return restRequester.execute(new AssetPairEndpoint(pair, info));
    }

    public Map<String, Ticker> ticker(List<String> pairs) {
        return restRequester.execute(new TickerEndpoint(pairs));
    }

    /* Implemented private endpoints */

    public LedgerInfo ledgerInfo(LedgerInfoParams params) {
        return restRequester.execute(new LedgerInfoEndpoint(params));
    }

    public Map<String, LedgerEntry> ledgerEntries(LedgerEntriesParams params) {
        return restRequester.execute(new LedgerEntriesEndpoint(params));
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
        return restRequester.execute(new JsonPrivateEndpoint(endpoint.getPath()));
    }

    public JsonNode query(Private endpoint, Map<String, String> params) {
        return restRequester.execute(new JsonPrivateEndpoint(endpoint.getPath(), params));
    }

    public JsonNode queryPrivate(String path) {
        return restRequester.execute(new JsonPrivateEndpoint(path));
    }

    public JsonNode queryPrivate(String path, Map<String, String> params) {
        return restRequester.execute(new JsonPrivateEndpoint(path, params));
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
