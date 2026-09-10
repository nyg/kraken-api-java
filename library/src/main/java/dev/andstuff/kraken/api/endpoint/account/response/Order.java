package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The order returned by {@code OpenOrders, ClosedOrders and QueryOrders}.
 *
 * @param referenceId the reference id
 * @param userReference the user reference
 * @param clientOrderId the client order id
 * @param status the status
 * @param openTime the open time as an instant
 * @param startTime the start time as an instant
 * @param expireTime the expire time as an instant
 * @param description the description
 * @param timeInForce the time in force
 * @param volume the volume
 * @param executedVolume the executed volume
 * @param cost the cost
 * @param fee the fee
 * @param price the price
 * @param stopPrice the stop price
 * @param limitPrice the limit price
 * @param trigger the trigger
 * @param margin whether this is a margin order
 * @param miscellaneous the miscellaneous
 * @param senderSubId the sender sub id
 * @param orderFlags the order flags
 * @param trades the trades
 * @param closeTime the close time as an instant
 * @param reason the reason
 */
public record Order(@JsonProperty("refid") String referenceId,
                    @JsonProperty("userref") Long userReference,
                    @JsonProperty("cl_ord_id") String clientOrderId,
                    Status status,
                    @JsonProperty("opentm") Instant openTime,
                    @JsonProperty("starttm") Instant startTime,
                    @JsonProperty("expiretm") Instant expireTime,
                    @JsonProperty("descr") Description description,
                    @JsonProperty("time_in_force") TimeInForce timeInForce,
                    @JsonProperty("vol") BigDecimal volume,
                    @JsonProperty("vol_exec") BigDecimal executedVolume,
                    BigDecimal cost,
                    BigDecimal fee,
                    BigDecimal price,
                    @JsonProperty("stopprice") BigDecimal stopPrice,
                    @JsonProperty("limitprice") BigDecimal limitPrice,
                    Trigger trigger,
                    Boolean margin,
                    @JsonProperty("misc") String miscellaneous,
                    @JsonProperty("sender_sub_id") String senderSubId,
                    @JsonProperty("oflags") String orderFlags,
                    List<String> trades,
                    @JsonProperty("closetm") Instant closeTime,
                    String reason) {

    /**
     * The status values used by the {@code OpenOrders, ClosedOrders and QueryOrders} endpoint.
     */
    public enum Status {
        @JsonProperty("pending") PENDING,
        @JsonProperty("open") OPEN,
        @JsonProperty("closed") CLOSED,
        @JsonProperty("canceled") CANCELED,
        @JsonProperty("expired") EXPIRED,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The type values used by the {@code OpenOrders, ClosedOrders and QueryOrders} endpoint.
     */
    public enum Type {
        @JsonProperty("buy") BUY,
        @JsonProperty("sell") SELL,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The order type values used by the {@code OpenOrders, ClosedOrders and QueryOrders} endpoint.
     */
    public enum OrderType {
        @JsonProperty("market") MARKET,
        @JsonProperty("limit") LIMIT,
        @JsonProperty("iceberg") ICEBERG,
        @JsonProperty("stop-loss") STOP_LOSS,
        @JsonProperty("take-profit") TAKE_PROFIT,
        @JsonProperty("trailing-stop") TRAILING_STOP,
        @JsonProperty("stop-loss-limit") STOP_LOSS_LIMIT,
        @JsonProperty("take-profit-limit") TAKE_PROFIT_LIMIT,
        @JsonProperty("trailing-stop-limit") TRAILING_STOP_LIMIT,
        @JsonProperty("settle-position") SETTLE_POSITION,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The description returned by {@code OpenOrders, ClosedOrders and QueryOrders}.
     *
     * @param pair the pair
     * @param type the type
     * @param orderType the order type
     * @param price the price
     * @param price2 the price2
     * @param leverage the leverage
     * @param order the order
     * @param close the close
     * @param assetClass the asset class
     */
    public record Description(String pair,
                              Type type,
                              @JsonProperty("ordertype") OrderType orderType,
                              BigDecimal price,
                              BigDecimal price2,
                              String leverage,
                              String order,
                              String close,
                              @JsonProperty("aclass") String assetClass) {}

    /**
     * The time in force values used by the {@code OpenOrders, ClosedOrders and QueryOrders} endpoint.
     */
    public enum TimeInForce {
        @JsonProperty("gtc") GTC,
        @JsonProperty("ioc") IOC,
        @JsonProperty("gtd") GTD,
        @JsonProperty("fok") FOK,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The trigger values used by the {@code OpenOrders, ClosedOrders and QueryOrders} endpoint.
     */
    public enum Trigger {
        @JsonProperty("last") LAST,
        @JsonProperty("index") INDEX,
        @JsonEnumDefaultValue UNKNOWN
    }
}
