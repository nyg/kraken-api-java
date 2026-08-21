package dev.andstuff.kraken.example.earn;

import java.util.Map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssetCodes {

    private static final Map<String, String> LEGACY_CODES = Map.ofEntries(
            Map.entry("XXBT", "BTC"),
            Map.entry("XBT", "BTC"),
            Map.entry("XXDG", "DOGE"),
            Map.entry("XDG", "DOGE"),
            Map.entry("XXLM", "XLM"),
            Map.entry("XXMR", "XMR"),
            Map.entry("XXRP", "XRP"),
            Map.entry("XETH", "ETH"),
            Map.entry("XETC", "ETC"),
            Map.entry("XLTC", "LTC"),
            Map.entry("XNMC", "NMC"),
            Map.entry("XZEC", "ZEC"),
            Map.entry("XREP", "REP"),
            Map.entry("XMLN", "MLN"),
            Map.entry("ZUSD", "USD"),
            Map.entry("ZEUR", "EUR"),
            Map.entry("ZGBP", "GBP"),
            Map.entry("ZCAD", "CAD"),
            Map.entry("ZJPY", "JPY"),
            Map.entry("ZAUD", "AUD"),
            Map.entry("ZCHF", "CHF"));

    public static String normalize(String krakenCode) {
        String code = krakenCode.split("\\.")[0].replaceAll("\\d+$", "");
        return LEGACY_CODES.getOrDefault(code, code);
    }

    public static boolean isEarnWallet(String krakenCode) {
        return krakenCode.contains(".");
    }
}
