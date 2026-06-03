package it.collegio.models;

import java.time.LocalDateTime;

public class AccessLog {

    private int id;
    private String user;
    private String ipAddress;
    private String note;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;

    public AccessLog() {
    }

    public AccessLog(String user, String ipAddress, String note) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.note = note;
    }

    public AccessLog(int id, String user, String ipAddress, String note,
                     LocalDateTime loginTime, LocalDateTime logoutTime) {
        this.id = id;
        this.user = user;
        this.ipAddress = ipAddress;
        this.note = note;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }
}
