package dev.andstuff.kraken.api.neo.model.endpoint;

import java.util.Map;

public interface QueryParams {

    QueryParams EMPTY = Map::of;

    Map<String, String> toMap();
}
