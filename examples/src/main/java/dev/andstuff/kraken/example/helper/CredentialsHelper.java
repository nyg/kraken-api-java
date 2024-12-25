package dev.andstuff.kraken.example.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import dev.andstuff.kraken.api.rest.KrakenCredentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CredentialsHelper {

    public static KrakenCredentials readFromFile(String path) {
        try {
            InputStream stream = CredentialsHelper.class.getResourceAsStream(path);
            Properties properties = new Properties();
            properties.load(stream);
            return new KrakenCredentials(properties.getProperty("key"), properties.getProperty("secret"));
        }
        catch (IOException e) {
            throw new IllegalStateException(String.format("Could not read properties from file: %s", path));
        }
    }
}
