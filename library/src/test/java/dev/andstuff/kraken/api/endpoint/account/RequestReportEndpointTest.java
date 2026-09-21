package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
import dev.andstuff.kraken.api.endpoint.account.params.RequestReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRequest;

@ExtendWith(MockitoExtension.class)
class RequestReportEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        RequestReportEndpoint unit = new RequestReportEndpoint(RequestReportParams.builder().type(ReportType.LEDGERS).format(ReportFormat.TSV)
                .description("ledgers +/&=").fields("refid,time,amount").fromDate(Instant.ofEpochSecond(1695728276L)).toDate(Instant.ofEpochSecond(1695828276L)).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("report", "ledgers"),
                Map.entry("format", "TSV"),
                Map.entry("description", "ledgers +/&="),
                Map.entry("fields", "refid,time,amount"),
                Map.entry("starttm", "1695728276"),
                Map.entry("endtm", "1695828276")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/AddExport");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_request_every_field_as_csv_over_whole_history_when_using_defaults() {
        RequestReportEndpoint unit = new RequestReportEndpoint(RequestReportParams.builder().type(ReportType.TRADES).description("my_trades_1").build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("report", "trades"),
                Map.entry("format", "CSV"),
                Map.entry("description", "my_trades_1"),
                Map.entry("fields", "all")));
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        RequestReportEndpoint unit = new RequestReportEndpoint(RequestReportParams.builder().type(ReportType.TRADES).description("my_trades_1").build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/AddExport.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<ReportRequest> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        ReportRequest result = unit.unwrapResponse(response);

        assertThat(result.reportId()).isEqualTo("TCJA");
    }
}
