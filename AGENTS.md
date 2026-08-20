# Agent Instructions

Multi-module Maven project (`library` + `examples`) providing a Java client for the [Kraken REST API](https://docs.kraken.com/rest/). See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the request flow and component diagrams, and [docs/RELEASE.md](docs/RELEASE.md) for the release process.

## Build

```sh
mvn clean install          # build and install both modules
mvn clean package          # build without install
mvn -pl library package    # build only the library module
mvn -pl examples package   # build only the examples module
```

There are no tests in this project. CI runs `mvn clean package` on PRs targeting `master`.

Java 25 with Temurin is required (configured via `maven-compiler-plugin` with `<release>25</release>`).

## Layout

`KrakenAPI` is the entry point: typed methods for implemented endpoints, generic `query()` methods taking a `Public`/`Private` enum value and returning a `JsonNode`, and raw `queryPublic()`/`queryPrivate()` taking a path string.

Every endpoint extends `Endpoint<T>`, either `PublicEndpoint<T>` (GET on `/0/public/{path}`, parameters from `QueryParams`) or `PrivateEndpoint<T>` (POST on `/0/private/{path}`, parameters from `PostParams`, signed with a nonce-based HMAC). Concrete endpoints live in a domain package under `endpoint/` — `market/` for public market data, `account/` for private account data, `subaccount/` for subaccount management, `transparency/` for public pre- and post-trade data, `earn/` for earn strategies and allocations — and follow `{Name}Endpoint`, `params/{Name}Params`, `response/{ResponseType}`.

`KrakenRestRequester` performs the HTTP calls and can be swapped for another HTTP client. Responses are unwrapped from the Kraken `{error, result}` envelope by `KrakenResponse<T>`; ZIP responses (report exports) go through `Endpoint.processZipResponse()`.

## Conventions

- **Lombok** is used throughout: `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@NonNull`, `@With`, `@ToString`, `@Slf4j`. Annotation processing is configured in the parent POM.
- **Java records** for response types and `KrakenResponse`. Records use `@JsonProperty` for Kraken's naming conventions and `@JsonEnumDefaultValue` for forward-compatible enum deserialization.
- **OpenCSV** annotations (`@CsvBindByName`) on `LedgerEntry` enable both JSON API and CSV file parsing with the same record.
- Jackson is configured with `ACCEPT_CASE_INSENSITIVE_ENUMS`, `READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE`, and `FAIL_ON_UNKNOWN_PROPERTIES` disabled — always include `@JsonEnumDefaultValue UNKNOWN` on new enums.
- Everything public in the `library` module is published API: keep implementation helpers package-private, e.g. `RecordMappingStrategy`.
- **Javadoc** on every public type and method of the `library` module, since it ships as a javadoc jar. One sentence of purpose plus `@param`/`@return`/`@throws` where they add information, naming the Kraken endpoint being called. `doclint` is set to `all,-missing`, so obvious enum constants can be left undocumented. The `examples` module is not documented this way.
- Commit messages follow [Conventional Commits](https://www.conventionalcommits.org/). Release commits use `chore(release):` prefix.
- The `examples` module is excluded from Maven Central publishing. API keys go in `examples/src/main/resources/api-keys.properties` (not committed).
