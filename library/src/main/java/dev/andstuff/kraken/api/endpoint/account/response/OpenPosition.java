package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The open position returned by {@code OpenPositions}.
 *
 * @param orderId the order id
 * @param assetClass the asset class
 * @param positionStatus the position status
 * @param pair the pair
 * @param time the time as an instant
 * @param type the type
 * @param orderType the order type
 * @param cost the cost
 * @param fee the fee
 * @param volume the volume
 * @param closedVolume the closed volume
 * @param margin the margin
 * @param value the value
 * @param net the net
 * @param terms the terms
 * @param rolloverTime the rollover time as an instant, preserving Kraken's string representation
 * @param miscellaneous the miscellaneous
 * @param orderFlags the order flags
 */
public record OpenPosition(@JsonProperty("ordertxid") String orderId,
                           @JsonProperty("class") String assetClass,
                           @JsonProperty("posstatus") PositionStatus positionStatus,
                           String pair,
                           Instant time,
                           String type,
                           @JsonProperty("ordertype") String orderType,
                           BigDecimal cost,
                           BigDecimal fee,
                           @JsonProperty("vol") BigDecimal volume,
                           @JsonProperty("vol_closed") BigDecimal closedVolume,
                           BigDecimal margin,
                           BigDecimal value,
                           BigDecimal net,
                           String terms,
                           @JsonProperty("rollovertm") String rolloverTime,
                           @JsonProperty("misc") String miscellaneous,
                           @JsonProperty("oflags") String orderFlags) {

    /**
     * The position status values used by the {@code OpenPositions} endpoint.
     */
    public enum PositionStatus {
        @JsonProperty("open") OPEN,
        @JsonEnumDefaultValue UNKNOWN
    }
}
