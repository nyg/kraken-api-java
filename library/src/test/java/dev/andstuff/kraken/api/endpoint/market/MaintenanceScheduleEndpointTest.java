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

@ExtendWith(MockitoExtension.class)
class MaintenanceScheduleEndpointTest {

    @InjectMocks
    private MaintenanceScheduleEndpoint unit;

    @Test
    void should_use_public_get_without_parameters_when_requesting_the_schedule() {
        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/MaintenanceSchedule").hasNoParameters();
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
        assertThat(KrakenAPI.Public.MAINTENANCE_SCHEDULE.getPath()).isEqualTo("MaintenanceSchedule");
    }

    @Test
    void should_read_every_maintenance_event_field_when_decoding_documented_response() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/maintenance-schedule.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<MaintenanceSchedule> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        MaintenanceSchedule result = response.result().orElseThrow();

        assertThat(result.events()).containsExactly(new MaintenanceSchedule.Event(21, "Scheduled Maintenance - Website",
                Instant.parse("2026-05-11T09:00:00Z"), Instant.parse("2026-05-11T10:00:00Z"), 1740,
                MaintenanceSchedule.Phase.APPROACHING_30M, List.of(MaintenanceSchedule.Service.SPOT_TRADING),
                MaintenanceSchedule.OrderSubmission.ALLOWED, MaintenanceSchedule.RecommendedAction.REDUCE_ACTIVITY,
                Instant.parse("2026-05-11T08:55:00Z"), "https://status.kraken.com/incidents/b7k2r9wqmn41"));
    }

    @Test
    void should_return_empty_collections_when_no_entries_are_available() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"events":[]}}
                """;

        KrakenResponse<MaintenanceSchedule> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        MaintenanceSchedule result = response.result().orElseThrow();

        assertThat(result.events()).isEmpty();
    }

    @Test
    void should_retain_unknown_enums_and_absent_deadline_when_schedule_gains_new_values() throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/maintenance-schedule.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8).replace("approaching_30m", "future-phase")
                    .replace("spot_trading", "future-service").replace("allowed", "future-guidance")
                    .replace("reduce_activity", "future-action")
                    .replace("\"cancel_before_utc\": \"2026-05-11T08:55:00Z\",", "\"future_field\": true,");
        }

        KrakenResponse<MaintenanceSchedule> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        MaintenanceSchedule result = response.result().orElseThrow();

        assertThat(result.events().getFirst()).satisfies(event -> {
            assertThat(event.phase()).isEqualTo(MaintenanceSchedule.Phase.UNKNOWN);
            assertThat(event.affectedServices()).containsExactly(MaintenanceSchedule.Service.UNKNOWN);
            assertThat(event.orderSubmission()).isEqualTo(MaintenanceSchedule.OrderSubmission.UNKNOWN);
            assertThat(event.recommendedAction()).isEqualTo(MaintenanceSchedule.RecommendedAction.UNKNOWN);
            assertThat(event.cancelBefore()).isNull();
        });
    }
}
