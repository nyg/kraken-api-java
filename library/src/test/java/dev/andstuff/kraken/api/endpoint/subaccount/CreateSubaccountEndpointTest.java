package dev.andstuff.kraken.api.endpoint.subaccount;

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
import dev.andstuff.kraken.api.endpoint.subaccount.params.CreateSubaccountParams;

@ExtendWith(MockitoExtension.class)
class CreateSubaccountEndpointTest {

    @Test
    void should_encode_username_and_email_when_creating_subaccount() {
        CreateSubaccountEndpoint unit = new CreateSubaccountEndpoint(CreateSubaccountParams.of("abc123", "abc+123@example.com"));

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.of("nonce", "123456789", "username", "abc123", "email", "abc+123@example.com"));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/CreateSubaccount");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
        assertThat(unit.getContentType()).isEqualTo("application/x-www-form-urlencoded");
    }

    @Test
    void should_reject_missing_username_when_building_parameters() {
        assertThatThrownBy(() -> CreateSubaccountParams.of(null, "abc123@example.com")).isInstanceOf(NullPointerException.class).hasMessageContaining("username");
    }

    @Test
    void should_reject_missing_email_when_building_parameters() {
        assertThatThrownBy(() -> CreateSubaccountParams.of("abc123", null)).isInstanceOf(NullPointerException.class).hasMessageContaining("email");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        CreateSubaccountEndpoint unit = new CreateSubaccountEndpoint(CreateSubaccountParams.of("abc123", "abc123@example.com"));
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/subaccount/CreateSubaccount.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Boolean> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Boolean result = unit.unwrapResponse(response);

        assertThat(result).isTrue();
    }
}
