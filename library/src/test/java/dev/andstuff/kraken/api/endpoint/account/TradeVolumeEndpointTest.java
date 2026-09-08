package dev.andstuff.kraken.api.endpoint.account;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.account.params.*;
import dev.andstuff.kraken.api.endpoint.account.response.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TradeVolumeEndpointTest {

    @Test
    void should_omit_optional_values_when_not_supplied() {
        TradeVolumeEndpoint unit = new TradeVolumeEndpoint();

        assertThat(unit.encodedParamsWith("123")).isEqualTo("{\"nonce\":123}");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        TradeVolumeEndpoint unit = new TradeVolumeEndpoint(TradeVolumeParams.builder().build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(new Jdk8Module()).build();
        String json = Files.readString(Path.of("src/test/resources/account/TradeVolume.json"));

        KrakenResponse<TradeVolume> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        TradeVolume result = unit.unwrapResponse(response);

        assertThat(result.fees().get("XXBTZUSD").fee()).isEqualByComparingTo("0.1000");
        assertThat(result.fees().get("XXBTZUSD").nextVolume()).isNull();
        assertThat(result.feesMaker().get("XXBTZUSD").fee()).isEqualByComparingTo("0.0000");
    }

    @Test
    void should_encode_structured_pairs_when_requesting_non_forex_fees() throws Exception {
        TradeVolumeEndpoint unit = new TradeVolumeEndpoint(TradeVolumeParams.builder()
                .pairsWithClass(List.of(new TradeVolumeParams.Pair("TSLAx/USD", "equity_pair")))
                .feeInfo(false).feeSchedule(true).rebaseMultiplier(RebaseMultiplier.BASE).build());
        JsonMapper mapper = JsonMapper.builder().build();

        assertThat(mapper.readTree(unit.encodedParamsWith("12345678901234567890"))).isEqualTo(mapper.readTree("""
                {"nonce":12345678901234567890,"pair":[{"asset":"TSLAx/USD","aclass":"equity_pair"}],"fee-info":false,"fee_schedule":true,"rebase_multiplier":"base"}
                """));
        assertThat(unit.getContentType()).isEqualTo("application/json");
    }

    @Test
    void should_encode_pair_names_when_requesting_forex_fees() throws Exception {
        TradeVolumeEndpoint unit = new TradeVolumeEndpoint(TradeVolumeParams.builder().pairs(List.of("XBTUSD", "ETHUSD")).build());
        JsonMapper mapper = JsonMapper.builder().build();

        assertThat(mapper.readTree(unit.encodedParamsWith("123"))).isEqualTo(mapper.readTree("""
                {"nonce":123,"pair":"XBTUSD,ETHUSD"}
                """));
    }

    @Test
    void should_reject_ambiguous_pairs_when_both_formats_are_supplied() {
        TradeVolumeEndpoint unit = new TradeVolumeEndpoint(TradeVolumeParams.builder()
                .pairs(List.of("XBTUSD")).pairsWithClass(List.of(new TradeVolumeParams.Pair("XBTUSD", "forex"))).build());

        assertThatThrownBy(() -> unit.encodedParamsWith("123")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_parse_fee_schedules_when_requested() throws Exception {
        TradeVolumeEndpoint unit = new TradeVolumeEndpoint();
        JsonMapper mapper = JsonMapper.builder().addModule(new Jdk8Module())
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE).build();
        String json = """
                {"error":[],"result":{"asset_class":"future-class","volume_subaccounts":[{"iiban":"subaccount","volume":"0.0000000000123456789"}],"schedules":[{"pair":"XBTUSD","class":"volume","tiers":[{"maker_fee":"0.25","taker_fee":"0.4","min_spot_volume":"0","min_futures_volume":"10000","min_assets_on_platform":"0","active":true}]}]}}
                """;

        KrakenResponse<TradeVolume> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        TradeVolume result = unit.unwrapResponse(response);

        assertThat(result.assetClass()).isEqualTo(TradeVolume.AssetClass.UNKNOWN);
        assertThat(result.volumeSubaccounts().getFirst().volume()).isEqualByComparingTo("0.0000000000123456789");
        assertThat(result.schedules().getFirst().assetClass()).isEqualTo(TradeVolume.AssetClass.VOLUME);
        assertThat(result.schedules().getFirst().tiers().getFirst().active()).isTrue();
        assertThat(result.schedules().getFirst().tiers().getFirst().makerFee()).isEqualByComparingTo("0.25");
    }
}
