package dev.andstuff.kraken.api.endpoint.account.params;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TradeVolumeParamsTest {

    @Test
    void should_retain_structured_pairs_when_building_request_parameters() {
        TradeVolumeParams unit = TradeVolumeParams.builder()
                .pairsWithClass(List.of(new TradeVolumeParams.Pair("TSLAx/USD", "equity_pair"))).build();

        Map<String, Object> result = unit.params();

        assertThat(result).containsOnlyKeys("pair").containsEntry("pair", List.of(new TradeVolumeParams.Pair("TSLAx/USD", "equity_pair")));
    }

    @Test
    void should_join_pair_names_when_building_request_parameters() {
        TradeVolumeParams unit = TradeVolumeParams.builder().pairs(List.of("XBTUSD", "ETHUSD")).build();

        Map<String, Object> result = unit.params();

        assertThat(result).containsOnlyKeys("pair").containsEntry("pair", "XBTUSD,ETHUSD");
    }

    @Test
    void should_reject_ambiguous_pairs_when_building_request_parameters() {
        TradeVolumeParams unit = TradeVolumeParams.builder().pairs(List.of("XBTUSD"))
                .pairsWithClass(List.of(new TradeVolumeParams.Pair("TSLAx/USD", "equity_pair"))).build();

        assertThatThrownBy(unit::params).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Specify pairs or pairsWithClass, not both");
    }
}
