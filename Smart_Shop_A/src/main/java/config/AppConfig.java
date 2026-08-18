package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {
    private static final String CONFIG_FILE = "application.properties";

    private final Properties properties;

    private AppConfig(Properties properties) {
        this.properties = properties;
    }

    public static AppConfig load() {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException(CONFIG_FILE + " was not found on the classpath.");
            }
            properties.load(input);
            return new AppConfig(properties);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read " + CONFIG_FILE, exception);
        }
    }

    public String getDatabaseName() {
        return read("db.name", "vikas");
    }

    public String getDatabaseUser() {
        return read("db.user", "root");
    }

    public String getDatabasePassword() {
        return read("db.password", "Amma@7758");
    }

    public String getAdminUsername() {
        return read("admin.username", "admin");
    }

    public String getAdminPassword() {
        return read("admin.password", "admin123");
    }

    public String getServerUrl() {
        return "jdbc:mysql://" + read("db.host", "localhost") + ":" + read("db.port", "3306") + "/" + urlParameters();
    }

    public String getDatabaseUrl() {
        return "jdbc:mysql://" + read("db.host", "localhost") + ":" + read("db.port", "3306")
                + "/" + getDatabaseName() + urlParameters();
    }

    private String urlParameters() {
        String params = read("db.params", "");
        if (params.isBlank()) {
            return "";
        }
        return params.startsWith("?") ? params : "?" + params;
    }

    private String read(String key, String fallback) {
        String environmentKey = key.toUpperCase().replace('.', '_');
        String environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }
        return properties.getProperty(key, fallback).trim();
    }
}
