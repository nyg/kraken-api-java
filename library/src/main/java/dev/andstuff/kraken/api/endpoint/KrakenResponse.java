package dev.andstuff.kraken.api.endpoint;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.NullNode;

/**
 * The envelope every JSON response of the Kraken REST API is wrapped in.
 *
 * @param <T> the type the {@code result} field is deserialized into
 * @param error the errors returned by Kraken, empty when the request succeeded
 * @param result the payload of the response, empty when the request failed
 */
public record KrakenResponse<T>(List<String> error,
                                Optional<T> result) {

    /**
     * Returns the payload of the response.
     *
     * @return the payload, or an empty optional if the request failed
     */
    public Optional<T> result() {
        // TODO looks like an issue with jackson which returns Optional.of(NullNode.instance) instead of Optional.empty
        return result.map(res -> res.equals(NullNode.getInstance()) ? null : res);
    }
}
