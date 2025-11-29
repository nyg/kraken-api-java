package dev.andstuff.kraken.api.endpoint.market.response;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;

public class AssetPairs {

    private final Map<String, AssetPair> assetPairsByName;
    private final Map<String, AssetPair> assetPairsByAltName;

    @JsonCreator
    public AssetPairs(Map<String, AssetPair> assetPairs) {
        assetPairsByName = assetPairs;
        assetPairsByAltName = assetPairs.values().stream().collect(toMap(AssetPair::alternateName, identity()));
    }

    public Optional<AssetPair> findBy(String name) {
        return ofNullable(assetPairsByName.getOrDefault(name, assetPairsByAltName.get(name)));
    }
}
