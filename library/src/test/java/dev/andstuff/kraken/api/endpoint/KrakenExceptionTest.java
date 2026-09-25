package dev.andstuff.kraken.api.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KrakenExceptionTest {

    @Test
    void should_use_error_as_message_when_kraken_returns_one_error() {
        KrakenException unit = new KrakenException(List.of("EAPI:Invalid key"));

        assertThat(unit).hasMessage("EAPI:Invalid key");
        assertThat(unit.getErrors()).containsExactly("EAPI:Invalid key");
    }

    @Test
    void should_join_errors_in_message_when_kraken_returns_several_errors() {
        KrakenException unit = new KrakenException(List.of("EGeneral:Invalid arguments", "EGeneral:Permission denied"));

        assertThat(unit).hasMessage("EGeneral:Invalid arguments, EGeneral:Permission denied");
    }

    @Test
    void should_have_no_message_when_kraken_returns_no_error_field() {
        KrakenException unit = new KrakenException(null);

        assertThat(unit.getMessage()).isNull();
        assertThat(unit.getErrors()).isNull();
    }
}
