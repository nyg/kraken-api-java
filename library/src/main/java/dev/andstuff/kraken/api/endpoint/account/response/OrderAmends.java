package dev.andstuff.kraken.api.endpoint.account.response;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * The order amends returned by {@code OrderAmends}.
 *
 * @param count the count; null when the count is omitted
 * @param amends the amends
 */
public record OrderAmends(Long count,
                          List<Amendment> amends) {

    /**
     * The amend type values used by the {@code OrderAmends} endpoint.
     */
    public enum AmendType {
        @JsonProperty("original") ORIGINAL,
        @JsonProperty("user") USER,
        @JsonProperty("restated") RESTATED,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The amendment returned by {@code OrderAmends}.
     *
     * @param amendId the amend id
     * @param amendType the amend type
     * @param orderQty the order qty
     * @param displayQty the display qty
     * @param remainingQty the remaining qty
     * @param limitPrice the limit price
     * @param triggerPrice the trigger price
     * @param reason the reason
     * @param postOnly the post only
     * @param timestamp the amendment instant, decoded from epoch nanoseconds
     */
    public record Amendment(@JsonProperty("amend_id") String amendId,
                            @JsonProperty("amend_type") AmendType amendType,
                            @JsonProperty("order_qty") BigDecimal orderQty,
                            @JsonProperty("display_qty") BigDecimal displayQty,
                            @JsonProperty("remaining_qty") BigDecimal remainingQty,
                            @JsonProperty("limit_price") BigDecimal limitPrice,
                            @JsonProperty("trigger_price") BigDecimal triggerPrice,
                            String reason,
                            @JsonProperty("post_only") Boolean postOnly,
                            @JsonDeserialize(using = NanosecondsDeserializer.class) Instant timestamp) {}

    static class NanosecondsDeserializer extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            return Instant.ofEpochSecond(0, parser.getLongValue());
        }
    }
}
