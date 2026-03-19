package com.kipcollo.demo;

public class User {

    private final int id;
    private final String username;
    private final String password;
    private final String role; // "admin", "cashier1", "cashier2"
    private final String displayName;

    public User(int id, String username, String password, String role, String displayName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.displayName = displayName;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getDisplayName() { return displayName; }

    public boolean isAdmin() { return "admin".equalsIgnoreCase(role); }
}
