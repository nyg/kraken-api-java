package dev.andstuff.kraken.api.model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.NullNode;

public record KrakenResponse<T>(List<String> error,
                                Optional<T> result) {

    public Optional<T> result() {
        // FIXME looks like an issue with jackson which returns Optional.of(NullNode.instance) instead of Optional.empty
        return result.map(res -> res.equals(NullNode.getInstance()) ? null : res);
    }
}
