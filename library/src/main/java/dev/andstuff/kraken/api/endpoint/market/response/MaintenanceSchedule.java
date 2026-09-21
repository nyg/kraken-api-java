package dev.andstuff.kraken.api.endpoint.market.response;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The {@code MaintenanceSchedule} response, covering the next seven days.
 *
 * @param events scheduled events ordered by expected start time, empty when none are scheduled
 */
public record MaintenanceSchedule(List<Event> events) {

    /**
     * A scheduled maintenance event returned by the {@code MaintenanceSchedule} and {@code SystemStatus} endpoints.
     *
     * @param eventId stable event identifier
     * @param title event title
     * @param expectedStart expected start time
     * @param expectedEnd expected end time
     * @param timeToStart seconds until the start, evaluated when Kraken served the response
     * @param phase approach phase
     * @param affectedServices affected Kraken services
     * @param orderSubmission guidance about submitting orders
     * @param recommendedAction suggested client action
     * @param cancelBefore cancellation deadline, absent when not scheduled
     * @param sourceUrl link to the event on Kraken's status page
     */
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

    /**
     * The approach phase in a {@code MaintenanceSchedule} event.
     */
    public enum Phase {
        ANNOUNCED, REMINDER_24H, APPROACHING_30M, IMMINENT_5M, FINAL_WARNING_30S,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * A Kraken service affected by a scheduled maintenance event or an incident.
     */
    public enum Service {
        SPOT_WS, SPOT_REST, SPOT_FIX, SPOT_TRADING,
        FUTURES_WS, FUTURES_REST, FUTURES_FIX, FUTURES_TRADING, ALL,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * Order submission guidance in a {@code MaintenanceSchedule} event.
     */
    public enum OrderSubmission {
        ALLOWED, DISCOURAGED, BLOCKED,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * Suggested client action in a {@code MaintenanceSchedule} event.
     */
    public enum RecommendedAction {
        CONTINUE, REDUCE_ACTIVITY, CANCEL_OPEN_ORDERS, STOP_NEW_ORDERS,
        @JsonEnumDefaultValue UNKNOWN
    }
}
