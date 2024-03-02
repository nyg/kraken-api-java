package dev.andstuff.kraken.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A KrakenApi instance allows querying the Kraken API.
 *
 * @author nyg
 */
@Getter
@Setter
@NoArgsConstructor
public class KrakenApi {

    private static final String OTP = "otp";
    private static final String NONCE = "nonce";
    private static final String MICRO_SECONDS = "000";

    private String key;
    private String secret;

    /**
     * Query a public method of the API with the given parameters.
     *
     * @param method     the API method
     * @param parameters the method parameters
     * @return the API response
     * @throws IllegalArgumentException if the API method is null
     * @throws IOException              if the request could not be created or executed
     */
    public JsonNode queryPublic(Method method, Map<String, String> parameters) throws IOException {

        ApiRequest request = new ApiRequest();
        request.setMethod(method);

        if (parameters != null) {
            request.setParameters(parameters);
        }

        return request.execute();
    }

    /**
     * Query a public method of the API without any parameters.
     *
     * @param method the public API method
     * @return the API response
     * @throws IOException if the request could not be created or executed
     */
    public JsonNode queryPublic(Method method) throws IOException {
        return queryPublic(method, null);
    }

    /**
     * Query a private method of the API with the given parameters.
     *
     * @param method     the private API method
     * @param otp        the one-time password
     * @param parameters the method parameters
     * @return the API response
     * @throws IOException              if the request could not be created or executed
     * @throws NoSuchAlgorithmException if the SHA-256 or HmacSha512 algorithm
     *                                  could not be found
     * @throws InvalidKeyException      if the HMAC key is invalid
     */
    public JsonNode queryPrivate(Method method, String otp, Map<String, String> parameters)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        ApiRequest request = new ApiRequest();
        request.setKey(key);

        // clone parameter map
        parameters = parameters == null ? new HashMap<>() : new HashMap<>(parameters);

        // set OTP parameter
        if (otp != null) {
            parameters.put(OTP, otp);
        }

        // generate nonce
        String nonce = System.currentTimeMillis() + MICRO_SECONDS;
        parameters.put(NONCE, nonce);

        // set the parameters and retrieve the POST data
        String postData = request.setParameters(parameters);

        // create SHA-256 hash of the nonce and the POST data
        byte[] sha256 = KrakenUtils.sha256(nonce + postData);

        // set the API method and retrieve the path
        byte[] path = KrakenUtils.stringToBytes(request.setMethod(method));

        // decode the API secret, it's the HMAC key
        byte[] hmacKey = KrakenUtils.base64Decode(secret);

        // create the HMAC message from the path and the previous hash
        byte[] hmacMessage = KrakenUtils.concatArrays(path, sha256);

        // create the HMAC-SHA512 digest, encode it and set it as the request signature
        String hmacDigest = KrakenUtils.base64Encode(KrakenUtils.hmacSha512(hmacKey, hmacMessage));
        request.setSignature(hmacDigest);

        return request.execute();
    }

    /**
     * @see #queryPrivate(Method, String, Map)
     */
    public JsonNode queryPrivate(Method method) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return queryPrivate(method, null, null);
    }

    /**
     * @see #queryPrivate(Method, String, Map)
     */
    public JsonNode queryPrivate(Method method, String otp) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return queryPrivate(method, otp, null);
    }

    /**
     * @see #queryPrivate(Method, String, Map)
     */
    public JsonNode queryPrivate(Method method, Map<String, String> parameters)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        return queryPrivate(method, null, parameters);
    }

    /**
     * Represents an API method.
     *
     * @author nyg
     */
    public enum Method {

        /* Public methods */
        ASSET_PAIRS("AssetPairs", true),
        ASSETS("Assets", true),
        DEPTH("Depth", true),
        OHLC("OHLC", true),
        SPREAD("Spread", true),
        SYSTEM_STATUS("SystemStatus", true),
        TICKER("Ticker", true),
        TIME("Time", true),
        TRADES("Trades", true),

        // TODO Missing methods
        //        "/private/AccountTransfer",
        //        "/private/AddExport",
        //        "/private/AddOrderBatch",
        //        "/private/BalanceEx",
        //        "/private/CancelAll",
        //        "/private/CancelAllOrdersAfter",
        //        "/private/CancelOrderBatch",
        //        "/private/CreateSubaccount",
        //        "/private/DepositMethods",
        //        "/private/Earn/Allocate",
        //        "/private/Earn/AllocateStatus",
        //        "/private/Earn/Allocations",
        //        "/private/Earn/Deallocate",
        //        "/private/Earn/DeallocateStatus",
        //        "/private/Earn/Strategies",
        //        "/private/EditOrder",
        //        "/private/ExportStatus",
        //        "/private/GetWebSocketsToken",
        //        "/private/RemoveExport",
        //        "/private/RetrieveExport",
        //        "/private/Stake",
        //        "/private/Staking/Assets",
        //        "/private/Staking/Pending",
        //        "/private/Staking/Transactions",
        //        "/private/Unstake",
        //        "/private/WalletTransfer",
        //        "/private/WithdrawAddresses",
        //        "/private/WithdrawMethods",

        /* Private methods */
        ADD_ORDER("AddOrder", false),
        BALANCE("Balance", false),
        CANCEL_ORDER("CancelOrder", false),
        CLOSED_ORDERS("ClosedOrders", false),
        DEPOSIT_ADDRESSES("DepositAddresses", false),
        DEPOSIT_METHODS("DepositMethods", false),
        DEPOSIT_STATUS("DepositStatus", false),
        LEDGERS("Ledgers", false),
        OPEN_ORDERS("OpenOrders", false),
        OPEN_POSITIONS("OpenPositions", false),
        QUERY_LEDGERS("QueryLedgers", false),
        QUERY_ORDERS("QueryOrders", false),
        QUERY_TRADES("QueryTrades", false),
        TRADES_HISTORY("TradesHistory", false),
        TRADE_BALANCE("TradeBalance", false),
        TRADE_VOLUME("TradeVolume", false),
        WITHDRAW("Withdraw", false),
        WITHDRAW_CANCEL("WithdrawCancel", false),
        WITHDRAW_INFO("WithdrawInfo", false),
        WITHDRAW_STATUS("WithdrawStatus", false);

        public final String name;
        public final boolean isPublic;

        Method(String name, boolean isPublic) {
            this.name = name;
            this.isPublic = isPublic;
        }
    }
}
