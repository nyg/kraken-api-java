package dev.andstuff.kraken.api.neo.model;

import java.util.List;
import java.util.Optional;

public record KrakenResponse<T>(List<String> error,
                                Optional<T> result) {
}
