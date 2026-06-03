package it.collegio.dto;

import java.time.LocalDateTime;

public class AccessLogDettaglio {

    private String userNome;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
    private String roleNome;

    public AccessLogDettaglio() {
    }

    public String getUserNome() { return userNome; }
    public void setUserNome(String userNome) { this.userNome = userNome; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalDateTime logoutTime) { this.logoutTime = logoutTime; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getRoleNome() { return roleNome; }
    public void setRoleNome(String roleNome) { this.roleNome = roleNome; }
}
