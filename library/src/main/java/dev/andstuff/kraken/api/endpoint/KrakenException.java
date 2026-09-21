package dev.andstuff.kraken.api.endpoint;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * Unchecked exception thrown when Kraken answers a request with a non-empty {@code error} field, e.g. {@code EGeneral:Permission denied}, or a Funding (Beta) request with an HTTP error status, in which case the only error is the status code followed by the response body.
 */
@Getter
@ToString
public class KrakenException extends RuntimeException {

    private final List<String> errors;

    /**
     * Creates an exception holding the errors returned by Kraken.
     *
     * @param errors the errors, as returned by Kraken
     */
    public KrakenException(List<String> errors) {
        this.errors = errors;
    }
}
