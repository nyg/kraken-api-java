package dev.andstuff.kraken.api.neo.model.endpoint.market.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record AssetPair(@JsonProperty("altname") String alternateName,
                        @JsonProperty("wsname") String webSocketName,
                        @JsonProperty("aclass_base") String baseAssetClass,
                        @JsonProperty("base") String baseAsset,
                        @JsonProperty("aclass_quote") String quoteAssetClass,
                        @JsonProperty("quote") String quoteAsset,
                        @JsonProperty("pair_decimals") int pairDecimals,
                        @JsonProperty("cost_decimals") int costDecimals,
                        @JsonProperty("lot_decimals") int lotDecimals,
                        @JsonProperty("lot_multiplier") int lotMultiplier,
                        @JsonProperty("leverage_buy") List<Integer> leverageBuy,
                        @JsonProperty("leverage_sell") List<Integer> leverageSell,
                        @JsonProperty("fees") List<FeeSchedule> takerFees,
                        @JsonProperty("fees_maker") List<FeeSchedule> makerFees,
                        @JsonProperty("fee_volume_currency") String volumeCurrencyFee,
                        @JsonProperty("margin_call") int marginCallLevel,
                        @JsonProperty("margin_stop") int marginStopLevel,
                        @JsonProperty("ordermin") BigDecimal minimumOrderSize,
                        @JsonProperty("costmin") BigDecimal minimumOrderCost,
                        @JsonProperty("tick_size") BigDecimal tickSize,
                        @JsonProperty("status") Status status,
                        @JsonProperty("long_position_limit") int maxLongPositionSize,
                        @JsonProperty("short_position_limit") int maxShortPositionSize) {

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    public record FeeSchedule(BigDecimal volume, BigDecimal percentage) {}

    @Getter
    @RequiredArgsConstructor
    public enum Info {

        ALL("info"),
        LEVERAGE("leverage"),
        FEES("fees"),
        MARGIN("margin");

        private final String value;
    }

    public enum Status {
        ONLINE,
        CANCEL_ONLY,
        POST_ONLY,
        LIMIT_ONLY,
        REDUCE_ONLY
    }
}
