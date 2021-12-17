package com.example.tictactoe;

public class User {
    private String email;
    private String password;
    private String user_id;

    public User() {

    }

    public User(String email, String password, String user_id) {
        this.email = email;
        this.password = password;
        this.user_id = user_id;
    }
}
