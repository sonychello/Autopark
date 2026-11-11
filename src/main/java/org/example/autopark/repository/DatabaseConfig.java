package org.example.autopark.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DatabaseConfig {
    private final Properties properties;

    public DatabaseConfig() {
        Path path = Path.of("config/db.properties");
        try (InputStream config = Files.newInputStream(path)) {
            properties = new Properties();
            properties.load(config);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить конфигурацию БД", e);
        }
    }

    public String getUrl() {
        return (String) properties.get("db.url");
    }

    public String getUsername() {
        return (String) properties.get("db.username");
    }

    public String getPassword() {
        return (String) properties.get("db.password");
    }
}
