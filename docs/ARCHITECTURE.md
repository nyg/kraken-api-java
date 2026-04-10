# Library Architecture

## Overview

The library is a Java client for the [Kraken REST API](https://docs.kraken.com/rest/). It is organized around four core concepts:

- **`KrakenAPI`** — The main entry point. A facade that exposes typed methods for implemented endpoints and generic methods for any endpoint.
- **`Endpoint<T>`** — Represents a single API call. Knows its HTTP method, URL path, parameters, and response type. Splits into `PublicEndpoint<T>` (GET) and `PrivateEndpoint<T>` (POST with HMAC signing).
- **`KrakenRestRequester`** — Interface that performs the actual HTTP request and response parsing. `DefaultKrakenRestRequester` is the built-in implementation using `HttpsURLConnection`.
- **Params / Response types** — Each endpoint has dedicated parameter objects (`QueryParams` for public, `PostParams` for private) and response records deserialized via Jackson.

## Request Flow

### Public Endpoint

```mermaid
sequenceDiagram
    participant User
    participant KrakenAPI
    participant PublicEndpoint
    participant QueryParams
    participant RestRequester as DefaultKrakenRestRequester
    participant Kraken as Kraken API

    User->>KrakenAPI: assetInfo(["BTC", "ETH"])
    KrakenAPI->>PublicEndpoint: new AssetInfoEndpoint(assets)
    activate PublicEndpoint
    PublicEndpoint->>QueryParams: new AssetInfoParams(assets, "currency")
    deactivate PublicEndpoint

    KrakenAPI->>RestRequester: execute(endpoint)
    RestRequester->>PublicEndpoint: buildURL()
    PublicEndpoint->>QueryParams: toMap()
    QueryParams-->>PublicEndpoint: {asset: "BTC,ETH", aclass: "currency"}
    PublicEndpoint-->>RestRequester: https://api.kraken.com/0/public/Assets?asset=BTC,ETH&aclass=currency

    RestRequester->>Kraken: GET
    Kraken-->>RestRequester: {"error": [], "result": {…}}

    RestRequester->>RestRequester: deserialize into KrakenResponse<Map<String, AssetInfo>>
    RestRequester->>RestRequester: unwrap result or throw KrakenException
    RestRequester-->>KrakenAPI: Map<String, AssetInfo>
    KrakenAPI-->>User: Map<String, AssetInfo>
```

### Private Endpoint

```mermaid
sequenceDiagram
    participant User
    participant KrakenAPI
    participant PrivateEndpoint
    participant PostParams
    participant NonceGen as KrakenNonceGenerator
    participant Credentials as KrakenCredentials
    participant RestRequester as DefaultKrakenRestRequester
    participant Kraken as Kraken API

    User->>KrakenAPI: ledgerInfo(params)
    KrakenAPI->>PrivateEndpoint: new LedgerInfoEndpoint(params)
    KrakenAPI->>RestRequester: execute(endpoint, credentials, nonceGenerator)

    RestRequester->>NonceGen: generate()
    NonceGen-->>RestRequester: "1712750400000"

    RestRequester->>PrivateEndpoint: encodedParamsWith(nonce)
    PrivateEndpoint->>PostParams: setNonce(nonce)
    PrivateEndpoint->>PostParams: encoded()
    PostParams-->>RestRequester: "nonce=1712750400000&asset=BTC,ETH&…"

    RestRequester->>PrivateEndpoint: buildURL()
    PrivateEndpoint-->>RestRequester: https://api.kraken.com/0/private/Ledgers

    RestRequester->>Credentials: sign(url, nonce, encodedParams)
    Note over Credentials: SHA-256(nonce + params)<br/>HMAC-SHA512(base64(secret), path + sha256)
    Credentials-->>RestRequester: Base64 signature

    RestRequester->>Kraken: POST with API-Key + API-Sign headers
    Kraken-->>RestRequester: {"error": [], "result": {…}}

    RestRequester->>RestRequester: deserialize into KrakenResponse<LedgerInfo>
    RestRequester->>RestRequester: unwrap result or throw KrakenException
    RestRequester-->>KrakenAPI: LedgerInfo
    KrakenAPI-->>User: LedgerInfo
```

## Component Diagram

```mermaid
classDiagram
    class KrakenAPI {
        -KrakenCredentials credentials
        -KrakenNonceGenerator nonceGenerator
        -KrakenRestRequester restRequester
        +serverTime() ServerTime
        +assetInfo(assets) Map~String, AssetInfo~
        +ticker(pairs) Map~String, Ticker~
        +ledgerInfo(params) LedgerInfo
        +query(endpoint) JsonNode
        +queryPublic(path) JsonNode
        +queryPrivate(path) JsonNode
    }

    class Endpoint~T~ {
        <<abstract>>
        #String path
        -String httpMethod
        -TypeReference~T~ responseType
        +buildURL() URL
        +wrappedResponseType() JavaType
    }

    class PublicEndpoint~T~ {
        -QueryParams queryParams
        +buildURL() URL
    }

    class PrivateEndpoint~T~ {
        -PostParams postParams
        +encodedParamsWith(nonce) String
        +buildURL() URL
    }

    class KrakenRestRequester {
        <<interface>>
        +execute(PublicEndpoint~T~) T
        +execute(PrivateEndpoint~T~, credentials, nonceGenerator) T
    }

    class DefaultKrakenRestRequester {
        -ObjectMapper OBJECT_MAPPER
    }

    class KrakenResponse~T~ {
        <<record>>
        +List~String~ error
        +Optional~T~ result
    }

    class KrakenException {
        +List~String~ errors
    }

    class KrakenCredentials {
        +sign(url, nonce, params) String
    }

    class KrakenNonceGenerator {
        <<interface>>
        +generate() String
    }

    Endpoint <|-- PublicEndpoint
    Endpoint <|-- PrivateEndpoint
    KrakenRestRequester <|.. DefaultKrakenRestRequester
    KrakenAPI --> KrakenRestRequester
    KrakenAPI --> KrakenCredentials
    KrakenAPI --> KrakenNonceGenerator
    DefaultKrakenRestRequester ..> KrakenResponse : deserializes
    KrakenResponse ..> KrakenException : throws on error
```

## Three Access Tiers

`KrakenAPI` provides three levels of access, from most to least typed:

| Tier | Methods | Return type | When to use |
|------|---------|-------------|-------------|
| **Typed** | `assetInfo()`, `ledgerInfo()`, etc. | Domain records | Endpoint has a dedicated implementation |
| **Enum-based** | `query(Public.TICKER, params)` | `JsonNode` | Endpoint is in the `Public`/`Private` enum but not yet typed |
| **Raw path** | `queryPublic("Trades", params)` | `JsonNode` | Endpoint isn't in the enum yet (e.g., newly added by Kraken) |

## Extending the Library

To add a new typed endpoint:

1. Create a response record in the appropriate `response/` package
2. Create a params class implementing `QueryParams` (public) or extending `PostParams` (private)
3. Create an endpoint class extending `PublicEndpoint<T>` or `PrivateEndpoint<T>`
4. Add a convenience method to `KrakenAPI`
