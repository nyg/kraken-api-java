package dev.andstuff.kraken.api.endpoint.market.response;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * The response of the {@code AssetPairs} endpoint, allowing pairs to be looked up by name or alternate name.
 */
public class AssetPairs {

    private final Map<String, AssetPair> assetPairsByName;
    private final Map<String, AssetPair> assetPairsByAltName;

    /**
     * Creates the response, indexing the given pairs by name and alternate name.
     *
     * @param assetPairs the asset pairs, by name, as returned by Kraken
     */
    @JsonCreator
    public AssetPairs(Map<String, AssetPair> assetPairs) {
        assetPairsByName = assetPairs;
        assetPairsByAltName = assetPairs.values().stream().collect(toMap(AssetPair::alternateName, identity()));
    }

    /**
     * Looks up an asset pair by name, then by alternate name.
     *
     * @param name the name of the pair, e.g. {@code XETHXXBT} or {@code ETHXBT}
     * @return the asset pair, or an empty optional if the response contains no such pair
     */
    public Optional<AssetPair> findBy(String name) {
        return ofNullable(assetPairsByName.getOrDefault(name, assetPairsByAltName.get(name)));
    }
}
