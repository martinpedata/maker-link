package com.example.makerlink.access;

public class SignUpValidity {
    private String age;
    private String name;
    private String email;
    private String password;
    private String username;

    public SignUpValidity(String age, String name, String email, String password, String username) {
        this.age = age;
        this.name = name;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public int checkValidity() {
        /// An error in the age format leads to error code 1, in the email format error code 2, etc... Code 0 is success.
        return 0;
    }
}
