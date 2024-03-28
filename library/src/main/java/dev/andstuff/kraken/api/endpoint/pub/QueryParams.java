package dev.andstuff.kraken.api.endpoint.pub;

import java.util.Map;

public interface QueryParams {

    QueryParams EMPTY = Map::of;

    Map<String, String> toMap();
}
