# Copilot Instructions

## Build

```sh
mvn clean install          # build and install both modules
mvn clean package          # build without install
mvn -pl library package    # build only the library module
mvn -pl examples package   # build only the examples module
```

There are no tests in this project. CI runs `mvn clean package` on PRs targeting `master`.

Java 25 with Temurin is required (configured via `maven-compiler-plugin` with `<release>25</release>`).

## Architecture

This is a multi-module Maven project (`library` + `examples`) providing a Java client for the [Kraken REST API](https://docs.kraken.com/rest/).

### Endpoint type hierarchy

All Kraken endpoints extend `Endpoint<T>` and split into two branches:

- **`PublicEndpoint<T>`** — GET requests to `/0/public/{path}`. Uses `QueryParams` to build URL query strings.
- **`PrivateEndpoint<T>`** — POST requests to `/0/private/{path}`. Uses `PostParams` for form-encoded body with nonce-based HMAC signing.

Concrete endpoints live in domain packages under `endpoint/`:
- `endpoint/market/` — public market data (assets, pairs, ticker, server time)
- `endpoint/account/` — private account data (ledgers, reports)

Each endpoint package follows: `{Name}Endpoint`, `params/{Name}Params`, `response/{ResponseType}`.

### KrakenAPI facade

`KrakenAPI` is the main entry point. It provides:
1. **Typed methods** for implemented endpoints (e.g., `assetInfo()`, `ledgerInfo()`)
2. **Generic `query()` methods** returning `JsonNode` for unimplemented endpoints, using the `Public`/`Private` enums
3. **Raw `queryPublic()`/`queryPrivate()`** accepting path strings for endpoints not yet in the enums

### REST requester

`KrakenRestRequester` is the HTTP abstraction. `DefaultKrakenRestRequester` uses `HttpsURLConnection`. The interface is designed for alternative HTTP client implementations (Spring RestTemplate, OkHttp, etc.).

JSON responses are deserialized through `KrakenResponse<T>`, which unwraps the Kraken `{error, result}` envelope. ZIP responses (report exports) go through `Endpoint.processZipResponse()`.

## Conventions

- **Lombok** is used throughout: `@Getter`, `@RequiredArgsConstructor`, `@Builder`, `@Slf4j`, `@With`, `@Setter`. Annotation processing is configured in the parent POM.
- **Java records** for response types and `KrakenResponse`. Records use `@JsonProperty` for Kraken's naming conventions and `@JsonEnumDefaultValue` for forward-compatible enum deserialization.
- **OpenCSV** annotations (`@CsvBindByName`) on `LedgerEntry` enable both JSON API and CSV file parsing with the same record.
- Jackson is configured with `ACCEPT_CASE_INSENSITIVE_ENUMS`, `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE`, and `FAIL_ON_UNKNOWN_PROPERTIES` disabled — always include `@JsonEnumDefaultValue UNKNOWN` on new enums.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/). Release commits use `chore(release):` prefix.
- The `examples` module is excluded from Maven Central publishing. API keys go in `examples/src/main/resources/api-keys.properties` (not committed).
