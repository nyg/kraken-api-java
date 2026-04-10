# Code Review — `library` Module

**Date:** 2026-04-10
**Reviewed by:** Claude Opus 4.6

---

## 1. Architecture

The library follows a clean, layered design:

- **`KrakenAPI`** — Facade class that exposes typed convenience methods for implemented endpoints, generic `query()` methods for unimplemented endpoints, and raw `queryPublic()`/`queryPrivate()` methods for fully custom paths. This three-tier access pattern provides good ergonomics while remaining extensible.
- **`Endpoint<T>` hierarchy** — `PublicEndpoint<T>` (GET) and `PrivateEndpoint<T>` (POST) handle URL construction, parameter encoding, and response type resolution. Each concrete endpoint is a small, focused class.
- **`KrakenRestRequester`** — Interface that decouples HTTP transport from the API logic, allowing users to swap `HttpsURLConnection` for other HTTP clients.
- **Params / Response separation** — Each domain package (`market/`, `account/`) cleanly separates endpoint definitions, request parameters, and response types.

**Strengths:**
- The endpoint-per-class pattern makes adding new Kraken endpoints straightforward and low-risk.
- The `KrakenResponse<T>` envelope unwrapping centralizes error handling.
- The generic `query()` escape hatch ensures the library is immediately usable for any Kraken endpoint, even before a typed implementation exists.
- Use of Java records for response types is idiomatic and eliminates boilerplate.

**Suggestions:**
- The `Public` and `Private` enums in `KrakenAPI` partially duplicate the typed endpoint classes. As more endpoints get typed implementations, consider whether these enums still serve a purpose or whether they add maintenance burden.
- `KrakenAPI` has many constructor overloads. The existing `@Builder` annotation could replace most of them, simplifying the API surface.

---

## 2. Code Quality

### Effective use of language features
- Java records, sealed-style enums with `@JsonEnumDefaultValue`, pattern matching in `LedgerEntry.underlyingAsset()`, and text blocks/formatted strings are used well throughout.
- Lombok is applied consistently (`@Getter`, `@RequiredArgsConstructor`, `@Builder`, `@Slf4j`, `@With`).

### Areas for improvement

**`PostParams.encoded()` readability** (`PostParams.java:17-31`)
The `reduce` with `StringBuilder` is harder to read than it needs to be. A simpler approach:
```java
return params.entrySet().stream()
    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
    .collect(Collectors.joining("&"));
```
This also avoids the `replaceFirst("&$", "")` trailing-ampersand cleanup.

**`KrakenCredentials` helper methods visibility** (`KrakenCredentials.java:38-43`)
`concat()` and `hmacSha512()` are `public static` but are implementation details of the signing process. They should be `private` (or package-private at most) to avoid polluting the public API.

**`AssetInfoParams.toMap()` uses `Map.of()`** (`AssetInfoParams.java:15-19`)
This always includes both `asset` and `aclass` parameters, even when they could be null. If `assets` is null, `String.join(",", null)` will throw a `NullPointerException`. Contrast with `AssetPairParams` which correctly uses `putIfNonNull`.

---

## 3. Potential Bugs

### `LedgerInfo.hasNext()` hardcodes page size (`LedgerInfo.java:22`)
```java
public boolean hasNext() {
    return entries.size() == 50;
}
```
This assumes the Kraken API always returns exactly 50 entries per page. If Kraken changes the page size, or if the last page happens to contain exactly 50 entries, this heuristic will break. Consider using the `count` field (which the record already contains) to determine whether more entries exist: `return resultOffset + entries.size() < count`.

### `RecordMappingStrategy` line length assertion (`RecordMappingStrategy.java:33-35`)
```java
if (recordComponents.length != line.length) {
    throw new CsvRuntimeException("Mismatch between line values and record components");
}
```
This will fail if the CSV contains extra columns not mapped to record components. OpenCSV's standard behavior is to silently ignore extra columns, but this strict check breaks that contract. If Kraken adds a new column to their CSV export, this will throw an exception rather than gracefully ignoring the extra data.

### `KrakenInstantConverter` fragile date parsing (`RecordMappingStrategy.java:73-75`)
```java
String[] dateTime = value.split(" ");
return Instant.parse("%sT%sZ".formatted(dateTime[0], dateTime[1]));
```
No bounds checking on the `dateTime` array. If the input is empty, contains no space, or has an unexpected format, this will throw an `ArrayIndexOutOfBoundsException` with no helpful message. Consider adding validation or using a `DateTimeFormatter`.

### Nonce collision risk with `EpochBasedNonceGenerator` (`EpochBasedNonceGenerator.java:6-8`)
Using `System.currentTimeMillis()` means two rapid sequential calls within the same millisecond produce the same nonce, which Kraken will reject. This is documented as customizable, but the default should be safer — for example, using `System.nanoTime()` or an `AtomicLong` counter seeded from the epoch.

### `PostParams.encoded()` iteration order (`PostParams.java:21`)
`params.keySet().stream()` iterates over a `HashMap`, which has no guaranteed order. While this doesn't affect correctness for the Kraken API (which doesn't care about parameter order), the nonce signature is computed over the encoded params string, meaning the signing in `KrakenCredentials.sign()` receives `urlEncodedParams` — if the `params()` method returns a different iteration order than `encoded()` produces, signing would fail. Currently this isn't a problem because `encoded()` builds the string itself, but it's a fragile coupling. Using a `LinkedHashMap` or `TreeMap` would make this deterministic.

---

## 4. Error Handling

### `KrakenException` missing message (`KrakenException.java`)
`KrakenException` extends `RuntimeException` but never calls `super(message)`. When this exception is caught and logged, `getMessage()` returns `null`, which makes log output less useful. Consider:
```java
public KrakenException(List<String> errors) {
    super(String.join(", ", errors));
    this.errors = errors;
}
```
The `@ToString` annotation helps in some contexts but won't be invoked by default logging frameworks.

### `IllegalStateException` used for multiple failure modes
`DefaultKrakenRestRequester` throws `IllegalStateException` for HTTP errors, URL construction errors, and unsupported content types. These are all different failure modes that callers may want to handle differently. Consider a dedicated exception hierarchy or at minimum distinct messages that allow programmatic differentiation.

### No HTTP status code checking (`DefaultKrakenRestRequester.java:83-98`)
`parseResponse()` reads from `connection.getInputStream()` without checking the HTTP status code. If the Kraken API returns a 4xx or 5xx status, `getInputStream()` will throw an `IOException`, and the actual error body (available via `getErrorStream()`) is lost. This makes debugging API errors harder than necessary.

### Missing `null` check on `credentials` for private endpoints (`KrakenAPI.java:146-148`)
```java
private <T> T executePrivate(PrivateEndpoint<T> endpoint) {
    return restRequester.execute(endpoint, credentials, nonceGenerator);
}
```
If `KrakenAPI` was constructed without credentials (using the no-arg constructor), calling any private endpoint method will pass `null` credentials to the requester, eventually causing a `NullPointerException` deep in the signing code. A clear error like `"Credentials required for private endpoints"` thrown early would be more helpful.

---

## 5. Suggestions for Improvement

1. **Add tests.** The library has zero test coverage. The endpoint classes, parameter encoding, response deserialization, nonce generation, and HMAC signing are all highly testable in isolation. The `KrakenRestRequester` interface makes it straightforward to mock the HTTP layer.

2. **Rate limiting.** The Kraken API has rate limits. Callers currently have no protection against hitting them. Consider adding optional rate-limiting support in `DefaultKrakenRestRequester` or documenting the limitation clearly.

3. **Retry logic.** Network failures and transient Kraken errors (e.g., `EService:Unavailable`) are not retried. An optional retry policy would improve robustness for production use.

4. **Resource cleanup in `DefaultKrakenRestRequester`.** `HttpsURLConnection` responses are not explicitly disconnected after reading. While not strictly required, calling `connection.disconnect()` in a `finally` block would release resources promptly.

5. **Thread safety of `PostParams`.** The `setNonce()` method mutates state on the `PostParams` instance, which means the same endpoint object cannot safely be reused across threads. Since endpoints are created per-call in `KrakenAPI`, this isn't currently a problem, but it's a subtle trap if the usage pattern changes. Making `encodedParamsWith()` return the encoded string without mutating the object would be cleaner.

6. **Consider `java.net.http.HttpClient`** (available since Java 11) as a replacement for `HttpsURLConnection`. It provides a more modern API with built-in async support and is the recommended approach for new Java HTTP code.
