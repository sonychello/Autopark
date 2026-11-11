package org.example.autopark.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig newConfig) {
        config = newConfig;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных", e);
        }
    }

}
