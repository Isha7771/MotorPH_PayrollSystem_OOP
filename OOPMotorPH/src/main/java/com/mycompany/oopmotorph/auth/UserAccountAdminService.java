package com.mycompany.oopmotorph.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserAccountAdminService {

    private final UserRepository userRepository;

    public UserAccountAdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> search(String keyword) throws IOException {
        String q = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<User> users = userRepository.findAll();
        if (q.isEmpty()) return users;

        return users.stream()
                .filter(u -> contains(u.getUsername(), q)
                        || contains(u.getEmployeeNo(), q)
                        || contains(u.getFullName(), q)
                        || contains(u.getRole().name(), q)
                        || contains(u.getPosition(), q))
                .collect(Collectors.toList());
    }

    public void updateCredentials(String employeeNo, String username, String password) throws IOException {
        String empNo = require(employeeNo, "Employee #");
        String newUsername = require(username, "Username");
        String newPassword = require(password, "Password");
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }

        List<User> users = new ArrayList<>(userRepository.findAll());
        Optional<User> existingOpt = users.stream()
                .filter(u -> empNo.equalsIgnoreCase(u.getEmployeeNo()))
                .findFirst();
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee account not found: " + empNo);
        }

        for (User u : users) {
            if (!empNo.equalsIgnoreCase(u.getEmployeeNo()) && newUsername.equalsIgnoreCase(u.getUsername())) {
                throw new IllegalArgumentException("Username already exists: " + newUsername);
            }
        }

        User existing = existingOpt.get();
        User updated = new User(
                newUsername,
                newPassword,
                existing.getRole(),
                existing.getEmployeeNo(),
                existing.getLastName(),
                existing.getFirstName(),
                existing.getPosition(),
                existing.getCategory()
        );

        for (int i = 0; i < users.size(); i++) {
            if (empNo.equalsIgnoreCase(users.get(i).getEmployeeNo())) {
                users.set(i, updated);
                break;
            }
        }
        userRepository.saveAll(users);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private String require(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " is required.");
        }
        return value.trim();
    }
}
