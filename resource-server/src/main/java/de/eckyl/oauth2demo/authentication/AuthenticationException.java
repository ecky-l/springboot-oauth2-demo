package de.eckyl.oauth2demo.authentication;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("not_authenticated");
    }
}
