Fixtures are representative excerpts from the response examples in Kraken's
[Spot REST OpenAPI specification](https://docs.kraken.com/openapi/spot-rest.yaml),
retrieved 2026-09-07. Order books and time series are shortened. `depth.json`
includes an extra property to check forward-compatible deserialization.

Tests also generate empty responses, display-name keys, future enum values,
and extra precision cases from these fixtures. No test calls the Kraken API.
