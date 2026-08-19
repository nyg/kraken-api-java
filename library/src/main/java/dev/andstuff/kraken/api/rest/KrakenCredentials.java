package dev.andstuff.kraken.api.rest;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The API key and secret of a Kraken account, used to sign private endpoint requests.
 */
@RequiredArgsConstructor
public class KrakenCredentials {

    @Getter
    @NonNull
    private final String key;

    @NonNull
    private final String secret;

    /**
     * Signs a private endpoint request, as described by Kraken's authentication documentation: the message is the endpoint path followed by the SHA-256 digest of the nonce and the request body, and it is signed with HMAC-SHA512 using the Base64 decoded secret.
     *
     * @param url the URL of the endpoint being queried
     * @param nonce the nonce of the request
     * @param urlEncodedParams the URL encoded request body, nonce included
     * @return the Base64 encoded signature, to be sent in the {@code API-Sign} header
     */
    public String sign(URL url, String nonce, String urlEncodedParams) {

        byte[] hmacKey = Base64.getDecoder().decode(secret);

        byte[] sha256 = sha256(nonce + urlEncodedParams);
        byte[] hmacMessage = concat(url.getPath().getBytes(StandardCharsets.UTF_8), sha256);

        byte[] hmac = hmacSha512(hmacKey, hmacMessage);
        return Base64.getEncoder().encodeToString(hmac);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] concat = new byte[a.length + b.length];
        System.arraycopy(a, 0, concat, 0, a.length);
        System.arraycopy(b, 0, concat, a.length, b.length);
        return concat;
    }

    private static byte[] hmacSha512(byte[] key, byte[] message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key, "HmacSHA512"));
            return mac.doFinal(message);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not compute HMAC digest", e);
        }
    }

    private static byte[] sha256(String message) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(message.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not compute SHA-256 digest", e);
        }
    }
}
