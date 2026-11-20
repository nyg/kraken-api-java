package dev.andstuff.kraken.api.endpoint.algotrading.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A page of terminal strategies returned by {@code GetClosedStrategies}.
 *
 * @param closed the strategies, newest first by closing time, or by opening time when querying by opening time
 * @param nextCursor the cursor of the next page, {@code null} on the last one
 */
public record ClosedStrategies(List<AlgoStrategy> closed,
                               @JsonProperty("next_cursor") String nextCursor) {}
