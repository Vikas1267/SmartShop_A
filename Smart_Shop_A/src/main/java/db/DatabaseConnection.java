package db;

import config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final AppConfig CONFIG = AppConfig.load();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("MySQL JDBC driver is missing from the classpath.", exception);
        }
    }

    private DatabaseConnection() {
    }

    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getServerUrl(),
                CONFIG.getDatabaseUser(),
                CONFIG.getDatabasePassword()
        );
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                CONFIG.getDatabaseUrl(),
                CONFIG.getDatabaseUser(),
                CONFIG.getDatabasePassword()
        );
    }

    public static AppConfig config() {
        return CONFIG;
    }
}
