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

    @Test
    void should_match_reference_digest_when_signing_a_funding_path_with_query() {
        KrakenCredentials unit = new KrakenCredentials("public-example", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==");

        String result = unit.sign("/funding/v1/fees/d4ec4d52-b159-428e-ba64-f45455a978a1?amount=5&fee_included=true", "1616492376594", "");

        assertThat(result).isEqualTo("+EH4mm7c+g2d6XOdsx+0rElDQTXFVxuzLIYLgZJwEe80Wi1TMWKDRoQA1dIXD/QFCGEwtvrp32APAoXMXT2k3w==");
    }

    @Test
    void should_match_reference_digest_when_signing_a_funding_json_body() {
        KrakenCredentials unit = new KrakenCredentials("public-example", "kQH5HW/8p1uGOVjbgWA7FunAmGO8lsSUXNsu3eow76sz84Q18fWxnyRzBHCd3pd5nE9qa99HAZtuZuj6F1huXg==");
        String body = "{\"scope\":{\"network_id\":\"d9d375da-44b7-4be1-8a00-8b281acfe366\"},\"address_details\":{\"crypto\":{\"address\":\"0xBef7B36845cA31045E86D0B46DBCac4e6752\"}},\"name\":\"Personal Wallet\"}";

        String result = unit.sign("/funding/v1/addresses", "1616492376594", body);

        assertThat(result).isEqualTo("Lc9l+liE00GWbh8QHewQEfGH3S6pYOjHIiTRyb9s9/Cynw9lIc1Y/z7LW+u8XUAVZ7AUdCrK7wmWYmBXHBp7Kg==");
    }
}
