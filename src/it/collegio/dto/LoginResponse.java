package it.collegio.dto;

import it.collegio.models.User;

public class LoginResponse {

    private boolean successful;
    private User user;
    private String errorMessage;

    public LoginResponse() {
    }

    public LoginResponse(boolean successful, User user) {
        this.successful = successful;
        this.user = user;
    }

    public LoginResponse(boolean successful, User user, String errorMessage) {
        this.successful = successful;
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public static LoginResponse ok(User user) {
        return new LoginResponse(true, user, null);
    }

    public static LoginResponse error(String message) {
        return new LoginResponse(false, null, message);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
