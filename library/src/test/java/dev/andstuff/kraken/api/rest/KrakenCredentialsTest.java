package dev.andstuff.kraken.api.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KrakenCredentialsTest {

    @ParameterizedTest
    @ValueSource(strings = {"https://api.kraken.com/0/private/AddOrder", "https://api.kraken.com/0/private/AddOrder?account_id=wallet"})
    void should_match_documented_signature_when_signing_the_uri_path(String endpointUrl) throws Exception {
        KrakenCredentials unit = new KrakenCredentials("public-example", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==");
        URL url = URI.create(endpointUrl).toURL();
        String body = "nonce=1616492376594&ordertype=limit&pair=XBTUSD&price=37500&type=buy&volume=1.25";

        String result = unit.sign(url, "1616492376594", body);

        assertThat(result).isEqualTo("4/dpxb3iT4tp/ZCVEwSnEsLxx0bqyhLpdfOpc6fn7OR8+UClSV5n9E6aSS8MPtnRfp32bAb0nmbRn6H8ndwLUQ==");
    }

    @Test
    void should_match_reference_digest_when_signing_a_json_body() throws Exception {
        KrakenCredentials unit = new KrakenCredentials("public-example", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==");
        URL url = URI.create("https://api.kraken.com/0/private/TradeVolume").toURL();
        String body = "{\"pair\":[{\"asset\":\"TSLAx/USD\",\"aclass\":\"equity_pair\"}],\"nonce\":123}";

        String result = unit.sign(url, "123", body);

        assertThat(result).isEqualTo("JRJDryVhouSrIMbOftfDi5V0ILl6Mm6LMoX/LfS28me0PEos74LxkJ18uW9lfw1Y7osOoM78imN0uJjti1DJRw==");
    }
}
