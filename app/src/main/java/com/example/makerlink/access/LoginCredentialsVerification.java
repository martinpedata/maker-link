package com.example.makerlink.access;

/**
 * Verify in the User table of the database whether the username and password are valid
 */
public class LoginCredentialsVerification {
    private String passwordInput;
    private String usernameInput;
    public LoginCredentialsVerification(String username, String password) {
        this.passwordInput = password;
        this.usernameInput = username;
    }

    public int checkValidityOfLogin() {
        // ... if bad return 0, else return 1.
        return 1;
    }
}
