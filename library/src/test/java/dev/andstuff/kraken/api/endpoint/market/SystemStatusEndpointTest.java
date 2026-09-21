package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.KrakenAPI;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.market.response.MaintenanceSchedule;
import dev.andstuff.kraken.api.endpoint.market.response.SystemStatus;

@ExtendWith(MockitoExtension.class)
class SystemStatusEndpointTest {

    @InjectMocks
    private SystemStatusEndpoint unit;

    @Test
    void should_use_public_get_without_parameters_when_requesting_system_status() {
        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/SystemStatus").hasNoParameters();
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(KrakenAPI.Public.SYSTEM_STATUS.getPath()).isEqualTo("SystemStatus");
    }

    @Test
    void should_read_status_and_incident_when_decoding_documented_emergency_response() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/system-status.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new SystemStatus(SystemStatus.Description.CANCEL_ONLY, Instant.parse("2026-05-08T14:28:00Z"), List.of(),
                List.of(new SystemStatus.Emergency(4821, "Elevated API error rates", SystemStatus.IncidentStatus.IDENTIFIED, SystemStatus.Impact.CRITICAL,
                        List.of(MaintenanceSchedule.Service.SPOT_WS, MaintenanceSchedule.Service.SPOT_REST, MaintenanceSchedule.Service.SPOT_FIX),
                        Instant.parse("2026-05-08T14:23:11Z"),
                        List.of(new SystemStatus.NextStep(List.of(MaintenanceSchedule.Service.SPOT_TRADING), SystemStatus.NextStep.Type.EXPECTED_RESTART,
                                Instant.parse("2026-05-08T14:30:00Z"))),
                        "https://stspg.io/c378t4f7rh0n"))));
    }

    @Test
    void should_read_online_status_when_system_operates_normally() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"status":"online","timestamp":"2023-07-06T18:52:00Z","upcoming_maintenance":[],"emergency":[]}}
                """;

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(SystemStatus.Description.ONLINE);
        assertThat(result.upcomingMaintenance()).isEmpty();
        assertThat(result.emergency()).isEmpty();
    }

    @Test
    void should_read_scheduled_event_when_maintenance_is_approaching() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"status":"online","timestamp":"2026-05-11T08:30:00Z","upcoming_maintenance":[{"event_id":21,"title":"Scheduled Maintenance - Website",
                "expected_start_utc":"2026-05-11T09:00:00Z","expected_end_utc":"2026-05-11T10:00:00Z","time_to_start_s":1740,"phase":"approaching_30m",
                "affected_services":["spot_trading"],"order_submission":"allowed","recommended_action":"reduce_activity","cancel_before_utc":"2026-05-11T08:55:00Z",
                "source_url":"https://status.kraken.com/incidents/b7k2r9wqmn41"}],"emergency":[]}}
                """;

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result.upcomingMaintenance()).containsExactly(new MaintenanceSchedule.Event(21, "Scheduled Maintenance - Website",
                Instant.parse("2026-05-11T09:00:00Z"), Instant.parse("2026-05-11T10:00:00Z"), 1740,
                MaintenanceSchedule.Phase.APPROACHING_30M, List.of(MaintenanceSchedule.Service.SPOT_TRADING),
                MaintenanceSchedule.OrderSubmission.ALLOWED, MaintenanceSchedule.RecommendedAction.REDUCE_ACTIVITY,
                Instant.parse("2026-05-11T08:55:00Z"), "https://status.kraken.com/incidents/b7k2r9wqmn41"));
        assertThat(result.emergency()).isEmpty();
    }

    @Test
    void should_fall_back_to_unknown_status_when_kraken_adds_a_trading_mode() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"status":"future-mode","timestamp":"2023-07-06T18:52:00Z"}}
                """;

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(SystemStatus.Description.UNKNOWN);
        assertThat(result.upcomingMaintenance()).isEmpty();
        assertThat(result.emergency()).isEmpty();
    }

    @Test
    void should_fall_back_to_unknown_values_when_kraken_adds_incident_states() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"status":"post_only","timestamp":"2026-05-08T14:28:00Z","emergency":[{"event_id":4822,"title":"Degraded order entry",
                "incident_status":"future-status","impact":"future-impact","affected_services":["future-service"],"started_at_utc":"2026-05-08T14:23:11Z",
                "next_steps":[{"applies_to":["all"],"type":"future-step","expected_at_utc":"2026-05-08T15:00:00Z"}]}]}}
                """;

        KrakenResponse<SystemStatus> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        SystemStatus result = unit.unwrapResponse(response);

        assertThat(result.status()).isEqualTo(SystemStatus.Description.POST_ONLY);
        assertThat(result.upcomingMaintenance()).isEmpty();
        assertThat(result.emergency()).singleElement().satisfies(emergency -> {
            assertThat(emergency.incidentStatus()).isEqualTo(SystemStatus.IncidentStatus.UNKNOWN);
            assertThat(emergency.impact()).isEqualTo(SystemStatus.Impact.UNKNOWN);
            assertThat(emergency.affectedServices()).containsExactly(MaintenanceSchedule.Service.UNKNOWN);
            assertThat(emergency.nextSteps()).containsExactly(new SystemStatus.NextStep(List.of(MaintenanceSchedule.Service.ALL),
                    SystemStatus.NextStep.Type.UNKNOWN, Instant.parse("2026-05-08T15:00:00Z")));
            assertThat(emergency.sourceUrl()).isNull();
        });
    }
}
