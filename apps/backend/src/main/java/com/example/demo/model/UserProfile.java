package com.example.demo.model;

import java.util.List;

public class UserProfile {
    private String id;
    private String username;
    private List<String> permissions;

    public UserProfile() {}
    public UserProfile(String id, String username, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.permissions = permissions;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
}
