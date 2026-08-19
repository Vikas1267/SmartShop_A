package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Exception.DataAccessException;
import Exception.DuplicateUserException;
import Exception.InvalidLoginException;
import Exception.UnauthorizedAccessException;
import Model.User;
import Model.UserCredentials;
import db.DatabaseConnection;

public class Userdao {
	
	public User create(User user, String passwordHash, String passwordSalt) {
        String sql = """
                INSERT INTO users (first_name, last_name, username, password_hash, password_salt, city, email, mobile)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getUsername());
            statement.setString(4, passwordHash);
            statement.setString(5, passwordSalt);
            statement.setString(6, user.getCity());
            statement.setString(7, user.getEmail());
            statement.setString(8, user.getMobile());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new User(
                            generatedKeys.getInt(1),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getUsername(),
                            user.getCity(),
                            user.getEmail(),
                            user.getMobile()
                    );
                }
            }
            throw new DataAccessException("User was saved, but the generated ID was not returned.");
        } catch (SQLException exception) {
            throw new DataAccessException("Unable to save user.");
        }
    }

    public boolean usernameExists(String username) throws DuplicateUserException {
        return exists("SELECT 1 FROM users WHERE username = ?", username);
    }

    public boolean emailExists(String email) throws DuplicateUserException {
        return exists("SELECT 1 FROM users WHERE email = ?", email);
    }

    public boolean mobileExists(String mobile) throws DuplicateUserException {
        return exists("SELECT 1 FROM users WHERE mobile = ?", mobile);
    }

    public Optional<UserCredentials> findCredentialsByUsername(String username) throws InvalidLoginException {
        String sql = """
                SELECT user_id, first_name, last_name, username, password_hash, password_salt, city, email, mobile
                FROM users
                WHERE username = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapUser(resultSet);
                    return Optional.of(new UserCredentials(
                            user,
                            resultSet.getString("password_hash"),
                            resultSet.getString("password_salt")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new InvalidLoginException("Unable to fetch login details.");
        }
    }

    public List<User> findAll() throws UnauthorizedAccessException {
        String sql = """
                SELECT user_id, first_name, last_name, username, city, email, mobile
                FROM users
                ORDER BY first_name, last_name, username
                """;
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
            return users;
        } catch (SQLException exception) {
            throw new UnauthorizedAccessException("Unable to fetch registered users.");
        }
    }

    private boolean exists(String sql, String value) throws DuplicateUserException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DuplicateUserException("Unable to check duplicate user data.");
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("user_id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("username"),
                resultSet.getString("city"),
                resultSet.getString("email"),
                resultSet.getString("mobile")
        );
    }

}
