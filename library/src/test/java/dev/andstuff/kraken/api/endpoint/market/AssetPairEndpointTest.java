package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.market.params.AssetPairParams;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPair;
import dev.andstuff.kraken.api.endpoint.market.response.AssetPairs;

@ExtendWith(MockitoExtension.class)
class AssetPairEndpointTest {

    @Test
    void should_omit_parameters_when_requesting_all_pairs() {
        AssetPairEndpoint unit = new AssetPairEndpoint();

        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/AssetPairs").hasNoParameters();
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_join_pairs_when_requesting_specific_pairs() {
        AssetPairEndpoint unit = new AssetPairEndpoint(List.of("ETH/BTC", "ETH/USD"));

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "ETH/BTC,ETH/USD").hasNoParameter("info");
    }

    @Test
    void should_encode_info_value_when_restricting_returned_information() {
        AssetPairEndpoint unit = new AssetPairEndpoint(List.of("XBTUSD"), AssetPairParams.Info.MARGIN);

        URL result = unit.buildURL();

        assertThat(result).hasParameter("pair", "XBTUSD").hasParameter("info", "margin");
    }

    @Test
    void should_find_pairs_by_name_and_alternate_name_when_decoding_documented_response() throws Exception {
        AssetPairEndpoint unit = new AssetPairEndpoint();
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/asset-pairs.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<AssetPairs> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AssetPairs result = unit.unwrapResponse(response);

        assertThat(result.findBy("XETHXXBT")).contains(new AssetPair("ETHXBT", "ETH/XBT", "currency", "XETH", "currency", "XXBT",
                5, 6, 8, 1, List.of(2, 3, 4, 5), List.of(2, 3, 4, 5), List.of(), List.of(), "ZUSD", 80, 40,
                new BigDecimal("0.01"), new BigDecimal("0.00002"), new BigDecimal("0.00001"), AssetPair.Status.ONLINE, 1100L, 400L));
        assertThat(result.findBy("XBTUSD")).hasValueSatisfying(pair -> {
            assertThat(pair.webSocketName()).isEqualTo("XBT/USD");
            assertThat(pair.tickSize()).isEqualByComparingTo("0.1");
        });
        assertThat(result.findBy("ETHXBT")).isEqualTo(result.findBy("XETHXXBT"));
        assertThat(result.findBy("DOGEUSD")).isEmpty();
    }

    @Test
    void should_read_fee_tiers_and_fall_back_to_unknown_status_when_decoding_fee_schedules() throws Exception {
        AssetPairEndpoint unit = new AssetPairEndpoint(List.of("XBTUSD"), AssetPairParams.Info.FEES);
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{"XXBTZUSD":{"altname":"XBTUSD","fees":[[0,0.40],[10000,0.35]],"fee_volume_currency":"ZUSD","status":"future-status"}}}
                """;

        KrakenResponse<AssetPairs> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        AssetPairs result = unit.unwrapResponse(response);

        assertThat(result.findBy("XBTUSD")).hasValueSatisfying(pair -> {
            assertThat(pair.takerFees()).containsExactly(new AssetPair.FeeSchedule(new BigDecimal("0"), new BigDecimal("0.40")),
                    new AssetPair.FeeSchedule(new BigDecimal("10000"), new BigDecimal("0.35")));
            assertThat(pair.makerFees()).isNull();
            assertThat(pair.status()).isEqualTo(AssetPair.Status.UNKNOWN);
        });
    }
}
