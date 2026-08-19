package service;

import config.AppConfig;
import db.DatabaseConnection;
import Model.Admin;
import Model.User;
import Utiil.*;

import java.util.List;
import java.util.Optional;

import DAO.Userdao;

public class AdminService {
    private final AppConfig config;
    private final Userdao userDao;

    public AdminService(Userdao userDao) {
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
