package com.tina.mail.pojo;

public class RegisterUser {
    private String username;
    private String email;

    public RegisterUser(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public RegisterUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
