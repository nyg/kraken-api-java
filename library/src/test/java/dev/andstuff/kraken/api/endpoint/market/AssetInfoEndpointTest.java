package dev.andstuff.kraken.api.endpoint.market;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

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
import dev.andstuff.kraken.api.endpoint.market.response.AssetInfo;

@ExtendWith(MockitoExtension.class)
class AssetInfoEndpointTest {

    @Test
    void should_default_to_currency_class_when_only_assets_are_provided() {
        AssetInfoEndpoint unit = new AssetInfoEndpoint(List.of("XBT", "ETH"));

        URL result = unit.buildURL();

        assertThat(result).hasProtocol("https").hasHost("api.kraken.com").hasPath("/0/public/Assets")
                .hasParameter("asset", "XBT,ETH").hasParameter("aclass", "currency");
        assertThat(unit.getHttpMethod()).isEqualTo("GET");
    }

    @Test
    void should_encode_asset_class_when_supplied() {
        AssetInfoEndpoint unit = new AssetInfoEndpoint(List.of("AAPLx"), "tokenized_asset");

        URL result = unit.buildURL();

        assertThat(result).hasParameter("asset", "AAPLx").hasParameter("aclass", "tokenized_asset");
    }

    @Test
    void should_read_every_asset_field_when_decoding_documented_response() throws Exception {
        AssetInfoEndpoint unit = new AssetInfoEndpoint(List.of("XBT", "EUR", "USD"));
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/market/assets.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<Map<String, AssetInfo>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, AssetInfo> result = unit.unwrapResponse(response);

        assertThat(result).containsOnlyKeys("XXBT", "ZEUR", "ZUSD")
                .containsEntry("XXBT", new AssetInfo("currency", "XBT", 10, 5, new BigDecimal("1"), AssetInfo.AssetStatus.ENABLED));
        assertThat(result.get("ZUSD").displayedDecimals()).isEqualTo(2);
    }

    @Test
    void should_read_restricted_statuses_and_absent_collateral_when_assets_cannot_be_funded() throws Exception {
        AssetInfoEndpoint unit = new AssetInfoEndpoint(List.of("XBT", "ETH", "DOT"));
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module())
                .build();
        String json = """
                {"error":[],"result":{
                "XXBT":{"aclass":"currency","altname":"XBT","decimals":10,"display_decimals":5,"status":"deposit_only","margin_rate":0.02},
                "XETH":{"aclass":"currency","altname":"ETH","decimals":10,"display_decimals":5,"collateral_value":0.0000000000123456789,"status":"funding_temporarily_disabled"},
                "DOT":{"aclass":"currency","altname":"DOT","decimals":10,"display_decimals":8,"status":"future-status"}}}
                """;

        KrakenResponse<Map<String, AssetInfo>> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        Map<String, AssetInfo> result = unit.unwrapResponse(response);

        assertThat(result.get("XXBT")).satisfies(asset -> {
            assertThat(asset.status()).isEqualTo(AssetInfo.AssetStatus.DEPOSIT_ONLY);
            assertThat(asset.collateralValue()).isNull();
        });
        assertThat(result.get("XETH")).satisfies(asset -> {
            assertThat(asset.status()).isEqualTo(AssetInfo.AssetStatus.FUNDING_TEMPORARILY_DISABLED);
            assertThat(asset.collateralValue()).isEqualByComparingTo("0.0000000000123456789");
        });
        assertThat(result.get("DOT").status()).isEqualTo(AssetInfo.AssetStatus.UNKNOWN);
    }
}
