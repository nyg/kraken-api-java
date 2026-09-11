package dev.andstuff.kraken.api.endpoint.account;

import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.andstuff.kraken.api.endpoint.KrakenException;
import dev.andstuff.kraken.api.endpoint.KrakenResponse;
import dev.andstuff.kraken.api.endpoint.account.params.CreditLinesParams;
import dev.andstuff.kraken.api.endpoint.account.response.CreditLines;
import dev.andstuff.kraken.api.endpoint.priv.PrivateEndpoint;

/**
 * The private {@code CreditLines} endpoint for credit lines.
 */
public class CreditLinesEndpoint extends PrivateEndpoint<Optional<CreditLines>> {

    /**
     * Creates the {@code CreditLines} endpoint with default options.
     */
    public CreditLinesEndpoint() {
        this(CreditLinesParams.builder().build());
    }

    /**
     * Creates the {@code CreditLines} endpoint.
     *
     * @param params the request parameters
     */
    public CreditLinesEndpoint(CreditLinesParams params) {
        super("CreditLines", params, new TypeReference<>() {});
    }

    /**
     * Unwraps {@code CreditLines}, whose successful result can be null.
     *
     * @param response the response envelope
     * @return the credit details, or an empty optional when Kraken returns no credit lines
     * @throws KrakenException if Kraken reports an error
     */
    @Override
    public Optional<CreditLines> unwrapResponse(KrakenResponse<Optional<CreditLines>> response) {
        if (!response.error().isEmpty()) {
            throw new KrakenException(response.error());
        }
        return response.result().orElseGet(Optional::empty);
    }
}
