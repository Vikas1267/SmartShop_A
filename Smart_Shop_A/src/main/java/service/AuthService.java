package service;

import Model.User;

import Model.UserCredentials;
import Utiil.*;

import java.util.Optional;

import DAO.Userdao;

public class AuthService {
    private final Userdao userDao;

    public AuthService(Userdao userDao) {
        this.userDao = userDao;
    }

    public User register(
            String firstName,
            String lastName,
            String username,
            String password,
            String city,
            String email,
            String mobile
    ) {
        firstName = ValidationUtil.requireText("First name", firstName, 50);
        lastName = ValidationUtil.requireText("Last name", lastName, 50);
        username = ValidationUtil.requireText("Username", username, 50);
        city = ValidationUtil.requireText("City", city, 80);
        email = ValidationUtil.requireText("Email", email, 120);
        mobile = ValidationUtil.requireText("Mobile number", mobile, 10);

        if (ValidationUtil.isBlank(password) || password.trim().length() < 4) {
            throw new IllegalArgumentException("Password must contain at least 4 characters.");
        }
        ValidationUtil.requireEmail(email);
        ValidationUtil.requireMobile(mobile);

        if (userDao.usernameExists(username)) {
            throw new IllegalArgumentException("Username is already in use. Please choose another username.");
        }
        if (userDao.emailExists(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (userDao.mobileExists(mobile)) {
            throw new IllegalArgumentException("Mobile number is already registered.");
        }

        String salt = PasswordUtil.generateSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        User user = new User(0, firstName, lastName, username, city, email, mobile);
        return userDao.create(user, hash, salt);
    }

    public Optional<User> login(String username, String password) {
        username = ValidationUtil.requireText("Username", username, 50);
        if (ValidationUtil.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        Optional<UserCredentials> credentials = userDao.findCredentialsByUsername(username);
        if (credentials.isEmpty()) {
            return Optional.empty();
        }

        UserCredentials userCredentials = credentials.get();
        boolean passwordMatches = PasswordUtil.verifyPassword(
                password,
                userCredentials.getPasswordHash(),
                userCredentials.getPasswordSalt()
        );
        return passwordMatches ? Optional.of(userCredentials.getUser()) : Optional.empty();
    }
}
