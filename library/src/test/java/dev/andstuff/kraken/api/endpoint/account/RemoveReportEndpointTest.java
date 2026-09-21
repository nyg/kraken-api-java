package dev.andstuff.kraken.api.endpoint.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
import dev.andstuff.kraken.api.endpoint.account.params.RemovalType;
import dev.andstuff.kraken.api.endpoint.account.params.RemoveReportParams;
import dev.andstuff.kraken.api.endpoint.account.response.ReportRemoval;

@ExtendWith(MockitoExtension.class)
class RemoveReportEndpointTest {

    @Test
    void should_encode_identifier_and_delete_type_when_deleting_report() {
        RemoveReportEndpoint unit = new RemoveReportEndpoint(RemoveReportParams.of("id +/&=", RemovalType.DELETE));

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("nonce", "123456789", "id", "id +/&=", "type", "delete"));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/RemoveExport");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_encode_cancel_type_when_canceling_report() {
        RemoveReportEndpoint unit = new RemoveReportEndpoint(RemoveReportParams.of("TCJA", RemovalType.CANCEL));

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsEntry("type", "cancel");
    }

    @Test
    void should_reject_missing_reportId_when_building_parameters() {
        assertThatThrownBy(() -> RemoveReportParams.of(null, RemovalType.DELETE)).isInstanceOf(NullPointerException.class).hasMessageContaining("reportId");
    }

    @Test
    void should_reject_missing_type_when_building_parameters() {
        assertThatThrownBy(() -> RemoveReportParams.of("TCJA", null)).isInstanceOf(NullPointerException.class).hasMessageContaining("type");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        RemoveReportEndpoint unit = new RemoveReportEndpoint(RemoveReportParams.of("TCJA", RemovalType.DELETE));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/account/RemoveExport.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<ReportRemoval> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        ReportRemoval result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new ReportRemoval(true, false));
    }

    @Test
    void should_read_cancel_flag_when_canceling_report() throws Exception {
        RemoveReportEndpoint unit = new RemoveReportEndpoint(RemoveReportParams.of("TCJA", RemovalType.CANCEL));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json = """
                {"error":[],"result":{"cancel":true}}
                """;

        KrakenResponse<ReportRemoval> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        ReportRemoval result = unit.unwrapResponse(response);

        assertThat(result).isEqualTo(new ReportRemoval(false, true));
    }
}
