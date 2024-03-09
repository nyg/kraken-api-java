package dev.andstuff.kraken.api.model;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KrakenException extends RuntimeException {

    private final List<String> errors;

    public KrakenException(List<String> errors) {
        this.errors = errors;
    }
}
