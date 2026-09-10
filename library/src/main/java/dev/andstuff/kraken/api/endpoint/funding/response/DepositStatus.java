package dev.andstuff.kraken.api.endpoint.funding.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * The transactions returned by {@code DepositStatus} in either paginated or unpaginated form.
 *
 * @param deposits the transactions, ordered from newest to oldest
 * @param nextCursor the next-page token; null or empty when there is no next page
 */
@JsonDeserialize(using = DepositStatusDeserializer.class)
public record DepositStatus(List<Deposit> deposits,
                            @JsonProperty("next_cursor") String nextCursor) {}
