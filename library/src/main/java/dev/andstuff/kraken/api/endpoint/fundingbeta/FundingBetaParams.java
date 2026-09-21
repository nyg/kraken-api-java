package dev.andstuff.kraken.api.endpoint.fundingbeta;

import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import dev.andstuff.kraken.api.endpoint.pub.QueryParams;

/**
 * The parameters of a {@link FundingBetaEndpoint}: URL query parameters and, for endpoints taking one, a JSON request body. Nested query objects use Kraken's bracket notation, e.g. {@code asset[class]=currency}. The nonce is not part of the parameters: it is sent in the {@code API-Nonce} header.
 */
public abstract class FundingBetaParams implements QueryParams {

    /**
     * Parameters of an endpoint that takes neither query parameters nor a body.
     */
    public static final FundingBetaParams EMPTY = new FundingBetaParams() {

        @Override
        public Map<String, String> toMap() {
            return Map.of();
        }
    };

    private static final JsonMapper OBJECT_MAPPER = JsonMapper.builder().build();

    /**
     * Returns the request body as name/value pairs, using the names expected by Kraken. Values can be nested maps and lists.
     *
     * @return the body parameters, or {@code null} for an endpoint sending no body
     */
    protected Map<String, Object> body() {
        return null;
    }

    /**
     * Returns the request body encoded as a JSON object.
     *
     * @return the JSON request body, or an empty string for an endpoint sending no body
     * @throws IllegalStateException if a parameter cannot be serialized
     */
    public String encodedBody() {
        Map<String, Object> body = body();
        if (body == null) {
            return "";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        }
        catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot encode %s".formatted(getClass().getSimpleName()), e);
        }
    }

    /**
     * Adds a query parameter to the given map, unless its value is {@code null}.
     *
     * @param <T> the type of the parameter value
     * @param map the map to add the parameter to
     * @param key the parameter name, as expected by Kraken
     * @param value the parameter value, possibly {@code null}
     * @param apply the function converting the value to its string representation
     */
    protected static <T> void putIfNonNull(Map<String, String> map, String key, T value, Function<T, String> apply) {
        QueryParams.putIfNonNull(map, key, value, apply);
    }

    /**
     * Adds a body parameter to the given map, unless its value is {@code null}.
     *
     * @param map the map to add the parameter to
     * @param key the parameter name, as expected by Kraken
     * @param value the parameter value, possibly {@code null}
     */
    protected static void putIfNonNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }
}
