package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The account trade returned by {@code TradesHistory and QueryTrades}.
 *
 * @param orderId the order id
 * @param positionId the position id
 * @param pair the pair
 * @param time the time as Unix seconds
 * @param type the type
 * @param orderType the order type
 * @param price the price
 * @param cost the cost
 * @param fee the fee
 * @param volume the volume
 * @param margin the margin
 * @param leverage the leverage
 * @param miscellaneous the miscellaneous
 * @param ledgers the ledgers
 * @param tradeId the trade id
 * @param maker the maker
 * @param assetClass the asset class
 * @param tradeOrderType the trade order type
 * @param positionStatus the position status
 * @param closedPrice the closed price
 * @param closedCost the closed cost
 * @param closedFee the closed fee
 * @param closedVolume the closed volume
 * @param closedMargin the closed margin
 * @param net the net
 * @param trades the trades
 */
public record AccountTrade(@JsonProperty("ordertxid") String orderId,
        @JsonProperty("postxid") String positionId,
        String pair,
        BigDecimal time,
        String type,
        @JsonProperty("ordertype") String orderType,
        BigDecimal price,
        BigDecimal cost,
        BigDecimal fee,
        @JsonProperty("vol") BigDecimal volume,
        BigDecimal margin,
        String leverage,
        @JsonProperty("misc") String miscellaneous,
        List<String> ledgers,
        @JsonProperty("trade_id") Long tradeId,
        Boolean maker,
        @JsonProperty("aclass") String assetClass,
        @JsonProperty("tradeordertype") String tradeOrderType,
        @JsonProperty("posstatus") String positionStatus,
        @JsonProperty("cprice") BigDecimal closedPrice,
        @JsonProperty("ccost") BigDecimal closedCost,
        @JsonProperty("cfee") BigDecimal closedFee,
        @JsonProperty("cvol") BigDecimal closedVolume,
        @JsonProperty("cmargin") BigDecimal closedMargin,
        BigDecimal net,
        List<String> trades) {}
