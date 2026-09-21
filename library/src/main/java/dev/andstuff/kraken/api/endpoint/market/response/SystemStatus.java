package dev.andstuff.kraken.api.endpoint.market.response;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response of the {@code SystemStatus} endpoint. The advisories explain why a trading mode is in effect or coming, but the trading mode itself is only given by {@code status}.
 *
 * @param status the current status of the Kraken trading system
 * @param timestamp the time the status was last updated
 * @param upcomingMaintenance the maintenance events scheduled within the next 72 hours, by ascending start time, empty if there are none
 * @param emergency the unresolved incidents, empty if there are none
 */
public record SystemStatus(Description status,
                           Instant timestamp,
                           @JsonProperty("upcoming_maintenance") List<MaintenanceSchedule.Event> upcomingMaintenance,
                           List<Emergency> emergency) {

    /**
     * Creates the response, replacing advisory lists Kraken omits with empty ones.
     *
     * @param status the current status of the Kraken trading system
     * @param timestamp the time the status was last updated
     * @param upcomingMaintenance the scheduled maintenance events, possibly {@code null}
     * @param emergency the unresolved incidents, possibly {@code null}
     */
    public SystemStatus {
        upcomingMaintenance = Objects.requireNonNullElse(upcomingMaintenance, List.of());
        emergency = Objects.requireNonNullElse(emergency, List.of());
    }

    /**
     * The trading mode of the Kraken trading system.
     */
    public enum Description {
        ONLINE,
        MAINTENANCE,
        CANCEL_ONLY,
        POST_ONLY,

        @JsonEnumDefaultValue
        UNKNOWN
    }

    /**
     * An unplanned incident, relayed from Kraken's status page until it is resolved.
     *
     * @param eventId stable incident identifier
     * @param title incident title
     * @param incidentStatus lifecycle state of the incident
     * @param impact severity of the incident
     * @param affectedServices affected Kraken services
     * @param startedAt time the incident was opened
     * @param nextSteps forecast operational actions, by ascending time, empty if none was published
     * @param sourceUrl link to the incident on Kraken's status page
     */
    public record Emergency(@JsonProperty("event_id") long eventId,
                            String title,
                            @JsonProperty("incident_status") IncidentStatus incidentStatus,
                            Impact impact,
                            @JsonProperty("affected_services") List<MaintenanceSchedule.Service> affectedServices,
                            @JsonProperty("started_at_utc") Instant startedAt,
                            @JsonProperty("next_steps") List<NextStep> nextSteps,
                            @JsonProperty("source_url") String sourceUrl) {}

    /**
     * An operational action Kraken forecasts during an incident.
     *
     * @param appliesTo Kraken services the action applies to
     * @param type nature of the action
     * @param expectedAt forecast time of the action
     */
    public record NextStep(@JsonProperty("applies_to") List<MaintenanceSchedule.Service> appliesTo,
                           Type type,
                           @JsonProperty("expected_at_utc") Instant expectedAt) {

        /**
         * The nature of a forecast action.
         */
        public enum Type {
            EXPECTED_RESTART, EXPECTED_CANCEL_ONLY, EXPECTED_POST_ONLY, EXPECTED_ONLINE,
            @JsonEnumDefaultValue UNKNOWN
        }
    }

    /**
     * The lifecycle state of an incident.
     */
    public enum IncidentStatus {
        INVESTIGATING, IDENTIFIED, MONITORING,
        @JsonEnumDefaultValue UNKNOWN
    }

    /**
     * The severity of an incident, as published on Kraken's status page.
     */
    public enum Impact {
        NONE, MINOR, MAJOR, CRITICAL,
        @JsonEnumDefaultValue UNKNOWN
    }
}
