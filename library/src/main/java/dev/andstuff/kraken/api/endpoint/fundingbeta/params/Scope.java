package dev.andstuff.kraken.api.endpoint.fundingbeta.params;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;

/**
 * The scope of a withdrawal address or a filter of the Funding (Beta) API: a funding method, a network or a network group. Exactly one identifier is set; create scopes with {@link #method(String)}, {@link #network(String)} or {@link #networkGroup(String)}.
 *
 * @param methodId the funding method defining the scope
 * @param networkId the network defining the scope
 * @param networkGroupId the network group defining the scope
 */
public record Scope(@JsonProperty("method_id") String methodId,
                    @JsonProperty("network_id") String networkId,
                    @JsonProperty("network_group_id") String networkGroupId) {

    /**
     * Creates a scope restricted to a single funding method.
     *
     * @param methodId the funding method identifier
     * @return the scope
     */
    public static Scope method(@NonNull String methodId) {
        return new Scope(methodId, null, null);
    }

    /**
     * Creates a scope covering every funding method of a network.
     *
     * @param networkId the network identifier
     * @return the scope
     */
    public static Scope network(@NonNull String networkId) {
        return new Scope(null, networkId, null);
    }

    /**
     * Creates a scope covering every network of a network group, e.g. EVM networks.
     *
     * @param networkGroupId the network group identifier
     * @return the scope
     */
    public static Scope networkGroup(@NonNull String networkGroupId) {
        return new Scope(null, null, networkGroupId);
    }

    void requireMethodOrNetwork(String operation) {
        if (networkGroupId != null) {
            throw new IllegalArgumentException("%s accepts a method or network scope, not a network group".formatted(operation));
        }
    }

    void putQuery(Map<String, String> query, String key) {
        json().forEach((name, value) -> query.put("%s[%s]".formatted(key, name), value));
    }

    Map<String, String> json() {
        Map<String, String> json = new LinkedHashMap<>();
        if (methodId != null) {
            json.put("method_id", methodId);
        }
        if (networkId != null) {
            json.put("network_id", networkId);
        }
        if (networkGroupId != null) {
            json.put("network_group_id", networkGroupId);
        }
        if (json.size() != 1) {
            throw new IllegalArgumentException("Scope requires exactly one of methodId, networkId or networkGroupId");
        }
        return json;
    }
}
