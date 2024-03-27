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
    public static final String REFERENCE_PAIR = REFERENCE_ASSET + REFERENCE_ASSET;

    private static final int REFERENCE_ASSET_DECIMALS = 2;

    private final Map<String, BigDecimal> rates;

    public AssetRates(Map<String, Ticker> tickers) {
        rates = tickers.entrySet().stream().collect(toMap(Map.Entry::getKey, this::extractRate));
        rates.put(REFERENCE_PAIR, BigDecimal.ONE);
    }

    public BigDecimal evaluate(BigDecimal amount, String asset) {
        String newFormatPair = asset + REFERENCE_ASSET;
        String oldFormatPair = "X%sZ%s".formatted(asset, REFERENCE_ASSET);
        return Optional.ofNullable(rates.get(newFormatPair))
                .orElseGet(() -> rates.getOrDefault(oldFormatPair, BigDecimal.ZERO))
                .multiply(amount)
                .setScale(REFERENCE_ASSET_DECIMALS, RoundingMode.HALF_UP);
    }

    private BigDecimal extractRate(Map.Entry<String, Ticker> entry) {
        return entry.getValue().lastTrade().price();
    }
}
