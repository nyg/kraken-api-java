package dev.andstuff.kraken.api.endpoint.funding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.math.BigDecimal;
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
import dev.andstuff.kraken.api.endpoint.funding.params.DestinationWallet;
import dev.andstuff.kraken.api.endpoint.funding.params.SourceWallet;
import dev.andstuff.kraken.api.endpoint.funding.params.WalletTransferParams;
import dev.andstuff.kraken.api.endpoint.funding.response.FundingReference;

@ExtendWith(MockitoExtension.class)
class WalletTransferEndpointTest {

    @Test
    void should_encode_all_options_when_supplied() {
        WalletTransferEndpoint unit = new WalletTransferEndpoint(WalletTransferParams.builder().asset("id +/&=").sourceWallet(SourceWallet.SPOT_WALLET).destinationWallet(DestinationWallet.FUTURES_WALLET).amount(new BigDecimal("0.0000000012300")).build());

        Map<String, String> result = Arrays.stream(unit.encodedParamsWith("123456789").split("&"))
                .map(value -> value.split("=", 2))
                .collect(Collectors.toMap(value -> value[0], value -> URLDecoder.decode(value[1], StandardCharsets.UTF_8)));

        assertThat(result).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
                Map.entry("nonce", "123456789"),
                Map.entry("asset", "id +/&="),
                Map.entry("from", "Spot Wallet"),
                Map.entry("to", "Futures Wallet"),
                Map.entry("amount", "0.0000000012300")));
        assertThat(unit.buildURL().getPath()).isEqualTo("/0/private/WalletTransfer");
        assertThat(unit.getHttpMethod()).isEqualTo("POST");
    }

    @Test
    void should_reject_missing_asset_when_building_parameters() {
        WalletTransferParams.WalletTransferParamsBuilder builder = WalletTransferParams.builder().sourceWallet(SourceWallet.SPOT_WALLET).destinationWallet(DestinationWallet.FUTURES_WALLET).amount(new BigDecimal("0.0000000012300"));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("asset");
    }

    @Test
    void should_reject_missing_sourceWallet_when_building_parameters() {
        WalletTransferParams.WalletTransferParamsBuilder builder = WalletTransferParams.builder().asset("id +/&=").destinationWallet(DestinationWallet.FUTURES_WALLET).amount(new BigDecimal("0.0000000012300"));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("sourceWallet");
    }

    @Test
    void should_reject_missing_destinationWallet_when_building_parameters() {
        WalletTransferParams.WalletTransferParamsBuilder builder = WalletTransferParams.builder().asset("id +/&=").sourceWallet(SourceWallet.SPOT_WALLET).amount(new BigDecimal("0.0000000012300"));

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("destinationWallet");
    }

    @Test
    void should_reject_missing_amount_when_building_parameters() {
        WalletTransferParams.WalletTransferParamsBuilder builder = WalletTransferParams.builder().asset("id +/&=").sourceWallet(SourceWallet.SPOT_WALLET).destinationWallet(DestinationWallet.FUTURES_WALLET);

        assertThatThrownBy(builder::build).isInstanceOf(NullPointerException.class).hasMessageContaining("amount");
    }

    @Test
    void should_deserialize_typed_response_when_reading_kraken_fixture() throws Exception {
        WalletTransferEndpoint unit = new WalletTransferEndpoint(WalletTransferParams.builder().asset("id +/&=").sourceWallet(SourceWallet.SPOT_WALLET).destinationWallet(DestinationWallet.FUTURES_WALLET).amount(new BigDecimal("0.0000000012300")).build());
        JsonMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModules(new JavaTimeModule(), new Jdk8Module()).build();
        String json;
        try (InputStream fixture = getClass().getResourceAsStream("/funding/WalletTransfer.json")) {
            json = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
        }

        KrakenResponse<FundingReference> response = mapper.readValue(json, unit.wrappedResponseType(mapper.getTypeFactory()));
        FundingReference result = response.result().orElseThrow();

        assertThat(result.referenceId()).isEqualTo("FTQcuak-V6Za8qrWnhzTx67yYHz8Tg");
    }
}
