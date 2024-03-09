package dev.andstuff.kraken.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesHelper {

    public static Properties readFromFile(String path) {
        try {
            InputStream stream = Examples.class.getResourceAsStream(path);
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Could not read properties from file: %s", path));
        }
    }
}
