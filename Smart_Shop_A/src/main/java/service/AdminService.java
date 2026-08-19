package com.vikas.service;

import com.vikas.config.AppConfig;
import com.vikas.dao.UserDao;
import com.vikas.db.DatabaseConnection;
import com.vikas.model.Admin;
import com.vikas.model.User;
import com.vikas.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

public class AdminService {
    private final AppConfig config;
    private final UserDao userDao;

    public AdminService(UserDao userDao) {
        this.config = DatabaseConnection.config();
        this.userDao = userDao;
    }

    public Optional<Admin> login(String username, String password) {
        username = ValidationUtil.requireText("Username", username, 50);
        if (ValidationUtil.isBlank(password)) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (config.getAdminUsername().equals(username) && config.getAdminPassword().equals(password)) {
            return Optional.of(new Admin(username));
        }
        return Optional.empty();
    }

    public List<User> listRegisteredUsers() {
        return userDao.findAll();
    }
}
