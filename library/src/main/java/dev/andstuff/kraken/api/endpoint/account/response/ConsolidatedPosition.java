package dev.andstuff.kraken.api.endpoint.account.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The open margin positions of a pair, consolidated by {@code OpenPositions} with {@code consolidation=market}.
 *
 * @param pair the pair
 * @param assetClass the asset class
 * @param positions the number of positions consolidated into this entry
 * @param type the direction, {@code buy} or {@code sell}
 * @param leverage the average leverage, or {@code n/a}
 * @param cost the opening cost, in quote currency
 * @param fee the opening fee, in quote currency
 * @param volume the opening size, in base currency
 * @param closedVolume the closed quantity, in base currency
 * @param margin the initial margin consumed, in quote currency
 * @param value the current value of the remaining positions, when calculations are requested
 * @param net the unrealised profit or loss of the remaining positions, when calculations are requested
 */
public record ConsolidatedPosition(String pair,
                                   @JsonProperty("class") String assetClass,
                                   Integer positions,
                                   String type,
                                   String leverage,
                                   BigDecimal cost,
                                   BigDecimal fee,
                                   @JsonProperty("vol") BigDecimal volume,
                                   @JsonProperty("vol_closed") BigDecimal closedVolume,
                                   BigDecimal margin,
                                   BigDecimal value,
                                   BigDecimal net) {}
