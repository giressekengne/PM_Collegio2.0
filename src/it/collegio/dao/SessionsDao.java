package it.collegio.dao;

import it.collegio.models.Sessions;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionsDao {

    public static final int DURATA_ORE = 8;

    private DatabaseConnection dbConnection;

    public SessionsDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public static String generaToken() {
        String t = UUID.randomUUID().toString().replace("-", "");
        return (t + t).substring(0, 64);
    }

    public int insertSession(Sessions session) {
        String query = QueryContainer.queryInsSession;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, session.getUser());
            pst.setInt(2, session.getCommittente());
            pst.setInt(3, session.getRole());
            pst.setString(4, session.getToken());
            pst.setTimestamp(5, Timestamp.valueOf(session.getExpired()));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SessionsDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean invalidateSession(int sessionId) {
        String query = QueryContainer.queryInvalidateSession;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, sessionId);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(SessionsDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Sessions getByToken(String token) {
        Sessions session = null;
        String query = QueryContainer.queryGetSessionByToken;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, token);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    session = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SessionsDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return session;
    }

    public boolean isValid(Sessions session) {
        if (session == null || session.getExpired() == null) {
            return false;
        }
        return session.getExpired().isAfter(LocalDateTime.now());
    }

    private Sessions mapRow(ResultSet rs) throws SQLException {
        Sessions s = new Sessions();
        s.setId(rs.getInt("session_id"));
        s.setUser(rs.getString("user_id"));
        s.setCommittente(rs.getInt("committente_id"));
        s.setRole(rs.getInt("role_id"));
        s.setToken(rs.getString("token"));

        Timestamp expires = rs.getTimestamp("expires");
        if (expires != null) {
            s.setExpired(expires.toLocalDateTime());
        }
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            s.setDataCreazione(created.toLocalDateTime());
        }
        return s;
    }
}
