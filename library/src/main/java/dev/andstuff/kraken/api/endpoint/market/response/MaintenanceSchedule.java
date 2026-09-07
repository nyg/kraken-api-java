package dev.andstuff.kraken.api.endpoint.market.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MaintenanceSchedule(List<Event> events) {

    public record Event(@JsonProperty("event_id") long eventId,
                        String title,
                        @JsonProperty("expected_start_utc") Instant expectedStart,
                        @JsonProperty("expected_end_utc") Instant expectedEnd,
                        @JsonProperty("time_to_start_s") long timeToStart,
                        Phase phase,
                        @JsonProperty("affected_services") List<Service> affectedServices,
                        @JsonProperty("order_submission") OrderSubmission orderSubmission,
                        @JsonProperty("recommended_action") RecommendedAction recommendedAction,
                        @JsonProperty("cancel_before_utc") Instant cancelBefore,
                        @JsonProperty("source_url") String sourceUrl) {}

    public enum Phase {
        ANNOUNCED, REMINDER_24H, APPROACHING_30M, IMMINENT_5M, FINAL_WARNING_30S,
        @JsonEnumDefaultValue UNKNOWN
    }

    public enum Service {
        SPOT_WS, SPOT_REST, SPOT_FIX, SPOT_TRADING,
        FUTURES_WS, FUTURES_REST, FUTURES_FIX, FUTURES_TRADING, ALL,
        @JsonEnumDefaultValue UNKNOWN
    }

    public enum OrderSubmission {
        ALLOWED, DISCOURAGED, BLOCKED,
        @JsonEnumDefaultValue UNKNOWN
    }

    public enum RecommendedAction {
        CONTINUE, REDUCE_ACTIVITY, CANCEL_OPEN_ORDERS, STOP_NEW_ORDERS,
        @JsonEnumDefaultValue UNKNOWN
    }
}
