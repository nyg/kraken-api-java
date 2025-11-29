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

@RequiredArgsConstructor
public class KrakenCredentials {

    @Getter
    @NonNull
    private final String key;

    @NonNull
    private final String secret;

    public String sign(URL url, String nonce, String urlEncodedParams) {

        byte[] hmacKey = Base64.getDecoder().decode(secret);

        byte[] sha256 = sha256(nonce + urlEncodedParams);
        byte[] hmacMessage = concat(url.getPath().getBytes(StandardCharsets.UTF_8), sha256);

        byte[] hmac = hmacSha512(hmacKey, hmacMessage);
        return Base64.getEncoder().encodeToString(hmac);
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] concat = new byte[a.length + b.length];
        System.arraycopy(a, 0, concat, 0, a.length);
        System.arraycopy(b, 0, concat, a.length, b.length);
        return concat;
    }

    public static byte[] hmacSha512(byte[] key, byte[] message) {
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
