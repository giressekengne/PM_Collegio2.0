package it.collegio.dao;

import it.collegio.dto.AccessLogDettaglio;
import it.collegio.models.AccessLog;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessLogDao {

    private DatabaseConnection dbConnection;

    public AccessLogDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public int insertLog(AccessLog log) {
        String query = QueryContainer.queryInsLog;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, log.getUser());
            pst.setString(2, log.getIpAddress());
            pst.setString(3, log.getNote());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccessLogDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean updateLogout(int logId, String note) {
        String query = QueryContainer.queryUpdLog;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setString(1, note);
            pst.setInt(2, logId);

            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(AccessLogDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<AccessLog> getAll() {
        List<AccessLog> logs = new ArrayList<>();
        String query = QueryContainer.queryGetLogs;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                logs.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccessLogDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return logs;
    }

    public List<AccessLog> getByUser(String userCounter) {
        List<AccessLog> logs = new ArrayList<>();
        String query = QueryContainer.queryGetLogsByUser;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, userCounter);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccessLogDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return logs;
    }

    public List<AccessLogDettaglio> getDettagli() {
        List<AccessLogDettaglio> dettagli = new ArrayList<>();
        String query = QueryContainer.queryGetLogsDettagliati;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                AccessLogDettaglio d = new AccessLogDettaglio();
                d.setUserNome(rs.getString("user_nome"));
                Timestamp login = rs.getTimestamp("login_time");
                if (login != null) d.setLoginTime(login.toLocalDateTime());
                Timestamp logout = rs.getTimestamp("logout_time");
                if (logout != null) d.setLogoutTime(logout.toLocalDateTime());
                d.setIpAddress(rs.getString("ip_address"));
                d.setRoleNome(rs.getString("role_nome"));
                dettagli.add(d);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccessLogDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettagli;
    }

    private AccessLog mapRow(ResultSet rs) throws SQLException {
        AccessLog log = new AccessLog();
        log.setId(rs.getInt("log_id"));
        log.setUser(rs.getString("user_id"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setNote(rs.getString("note"));

        Timestamp login = rs.getTimestamp("login_time");
        if (login != null) {
            log.setLoginTime(login.toLocalDateTime());
        }
        Timestamp logout = rs.getTimestamp("logout_time");
        if (logout != null) {
            log.setLogoutTime(logout.toLocalDateTime());
        }
        return log;
    }
}
