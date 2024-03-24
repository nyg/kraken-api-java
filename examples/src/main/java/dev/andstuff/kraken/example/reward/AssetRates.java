package dev.andstuff.kraken.example.reward;

import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import dev.andstuff.kraken.api.model.endpoint.market.response.Ticker;
import lombok.Getter;

@Getter
public class AssetRates {

    public static final String REFERENCE_ASSET = "USD";
    private static final int REFERENCE_ASSET_DECIMALS = 2;

    private final Map<String, BigDecimal> rates;

    public AssetRates(Map<String, Ticker> tickers) {
        rates = tickers.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, this::extractRate));
        rates.put(REFERENCE_ASSET + REFERENCE_ASSET, BigDecimal.ONE);
    }

    public BigDecimal evaluate(BigDecimal amount, String asset) {
        String normalPair = asset + REFERENCE_ASSET;
        String commodityPair = "X%sZ%s".formatted(asset, REFERENCE_ASSET);
        BigDecimal rate = Optional.ofNullable(rates.get(normalPair))
                .orElseGet(() -> rates.getOrDefault(commodityPair, BigDecimal.ZERO));
        return rate.multiply(amount).setScale(REFERENCE_ASSET_DECIMALS, RoundingMode.HALF_UP);
    }

    private BigDecimal extractRate(Map.Entry<String, Ticker> entry) {
        return entry.getValue().lastTrade().price();
    }
}
