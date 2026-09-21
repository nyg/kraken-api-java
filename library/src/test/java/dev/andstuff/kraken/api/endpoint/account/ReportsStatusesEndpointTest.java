package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.account.params.ReportFormat;
import dev.andstuff.kraken.api.endpoint.account.params.ReportType;
import dev.andstuff.kraken.api.endpoint.account.params.ReportsStatusesParams;
import dev.andstuff.kraken.api.endpoint.account.response.Report;

@ExtendWith(MockitoExtension.class)
class ReportsStatusesEndpointTest {

    @Test
    void should_encode_report_type_when_listing_reports() {
        ReportsStatusesEndpoint unit = new ReportsStatusesEndpoint(ReportsStatusesParams.of(ReportType.LEDGERS));

        String result = unit.encodedParamsWith("123");

        assertThat(result).isEqualTo("report=ledgers&nonce=123");
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/ExportStatus");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_type_when_building_parameters() {
        assertThatThrownBy(() -> ReportsStatusesParams.of(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("type");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        ReportsStatusesEndpoint unit = new ReportsStatusesEndpoint(ReportsStatusesParams.of(ReportType.TRADES));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/ExportStatus.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<List<Report>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        List<Report> result = unit.unwrapResponse(response);

        assertThat(result).extracting(Report::id).containsExactly("VSKC", "TCJA");
        assertThat(result.getFirst()).isEqualTo(new Report("VSKC", "my_trades_1", ReportFormat.CSV, "all", Report.Status.PROCESSED, "all",
                Instant.ofEpochSecond(1688669085L), Instant.ofEpochSecond(1688669093L), Instant.ofEpochSecond(1688669093L),
                Instant.ofEpochSecond(1683556800L), Instant.ofEpochSecond(1688669085L), "all"));
        assertThat(result).allMatch(Report::isProcessed);
    }

    @Test
    void should_report_pending_generation_when_reports_are_not_processed_yet() throws Exception {
        ReportsStatusesEndpoint unit = new ReportsStatusesEndpoint(ReportsStatusesParams.of(ReportType.LEDGERS));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":[
                {"id":"QUEUED","format":"TSV","status":"Queued","createdtm":"1688669085","starttm":"0","completedtm":"0"},
                {"id":"PROCESSING","format":"CSV","status":"Processing","createdtm":"1688669085","starttm":"1688669093","completedtm":"0"},
                {"id":"FUTURE","format":"CSV","status":"future-status","createdtm":"1688669085"}]}
                """;

        KrakenResponse<List<Report>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        List<Report> result = unit.unwrapResponse(response);

        assertThat(result).extracting(Report::status).containsExactly(Report.Status.QUEUED, Report.Status.PROCESSING, Report.Status.UNKNOWN);
        assertThat(result).noneMatch(Report::isProcessed);
        assertThat(result.getFirst().format()).isEqualTo(ReportFormat.TSV);
        assertThat(result.getFirst().completionDate()).isEqualTo(Instant.EPOCH);
    }
}
