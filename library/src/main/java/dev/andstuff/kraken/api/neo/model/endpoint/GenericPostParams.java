package dev.andstuff.kraken.api.neo.model.endpoint;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenericPostParams implements PostParams {

    private final Map<String, String> params;

}
