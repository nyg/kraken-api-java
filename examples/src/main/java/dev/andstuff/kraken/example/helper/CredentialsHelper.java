package dev.andstuff.kraken.example.helper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import dev.andstuff.kraken.api.rest.KrakenCredentials;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CredentialsHelper {

    public static KrakenCredentials readFromFile(String path) {
        try (InputStream stream = open(path)) {
            Properties properties = new Properties();
            properties.load(stream);
            return new KrakenCredentials(properties.getProperty("key"), properties.getProperty("secret"));
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not read properties from file: %s".formatted(path), e);
        }
    }

    private static InputStream open(String path) throws IOException {
        InputStream stream = CredentialsHelper.class.getResourceAsStream(path);
        return stream != null ? stream : Files.newInputStream(findNearby(path));
    }

    private static Path findNearby(String path) {
        String fileName = Path.of(path).getFileName().toString();

        for (Path directory = Path.of("").toAbsolutePath(); directory != null; directory = directory.getParent()) {
            Path candidate = directory.resolve(fileName);
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException("Could not find %s on the classpath, nor in %s or any of its parent directories. Copy examples/src/main/resources/api-keys.properties.example to api-keys.properties and fill in your API keys."
                .formatted(fileName, Path.of("").toAbsolutePath()));
    }
}
