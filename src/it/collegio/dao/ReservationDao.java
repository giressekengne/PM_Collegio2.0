package it.collegio.dao;

import it.collegio.dto.ReservationDettaglio;
import it.collegio.enums.ReservationStatus;
import it.collegio.models.Reservation;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationDao {

    private DatabaseConnection dbConnection;

    public ReservationDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public int insertReservation(Reservation reservation) {
        String query = QueryContainer.queryInsReservation;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, reservation.getUser());
            pst.setInt(2, reservation.getCommittente());
            pst.setInt(3, reservation.getRoom());
            pst.setDate(4, toSqlDate(reservation.getCheckIn()));
            pst.setDate(5, toSqlDate(reservation.getCheckOut()));
            pst.setInt(6, reservation.getGiorni());
            pst.setString(7, reservation.getStato() != null ? reservation.getStato().name().toLowerCase() : ReservationStatus.ATTIVA.name().toLowerCase());
            pst.setString(8, reservation.getNote());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        int generatedId = keys.getInt(1);
                        reservation.setId(String.valueOf(generatedId));
                        return generatedId;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean updateReservation(Reservation reservation) {
        String query = QueryContainer.queryUpdReservation;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setInt(1, reservation.getRoom());
            pst.setDate(2, toSqlDate(reservation.getCheckIn()));
            pst.setDate(3, toSqlDate(reservation.getCheckOut()));
            pst.setInt(4, reservation.getGiorni());
            pst.setString(5, reservation.getStato() != null ? reservation.getStato().name().toLowerCase() : ReservationStatus.ATTIVA.name().toLowerCase());
            pst.setString(6, reservation.getNote());
            pst.setString(7, reservation.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Reservation getById(String id) {
        Reservation reservation = null;
        String query = QueryContainer.queryGetReservationById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    reservation = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reservation;
    }

    public List<Reservation> getByUser(String userCounter) {
        List<Reservation> reservations = new ArrayList<>();
        String query = QueryContainer.queryGetReservationsByUser;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, userCounter);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reservations;
    }

    public List<Reservation> getByCommittente(int committenteId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = QueryContainer.queryGetReservationsByCommittente;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, committenteId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reservations;
    }

    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String query = QueryContainer.queryGetReservations;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                reservations.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reservations;
    }

    public ReservationDettaglio getDettaglio(int reservationId, String userCounterFilter) {
        ReservationDettaglio dettaglio = null;
        String query = userCounterFilter != null
                ? QueryContainer.queryGetReservationDettaglioByUser
                : QueryContainer.queryGetReservationDettaglio;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, reservationId);
            if (userCounterFilter != null) {
                pst.setString(2, userCounterFilter);
            }
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    dettaglio = mapDettaglio(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettaglio;
    }

    public List<ReservationDettaglio> getDettagli() {
        List<ReservationDettaglio> dettagli = new ArrayList<>();
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryGetReservationDettagli);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                dettagli.add(mapDettaglio(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettagli;
    }

    public List<ReservationDettaglio> getDettagliByUser(String userCounter) {
        List<ReservationDettaglio> dettagli = new ArrayList<>();
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryGetReservationDettagliByUser)) {
            pst.setString(1, userCounter);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    dettagli.add(mapDettaglio(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettagli;
    }

    private ReservationDettaglio mapDettaglio(ResultSet rs) throws SQLException {
        ReservationDettaglio d = new ReservationDettaglio();
        d.setReservationId(rs.getInt("reservation_id"));
        d.setUserNome(rs.getString("user_nome"));
        d.setCommittenteId(rs.getInt("committente_id"));
        d.setRoomId(rs.getInt("room_id"));
        d.setPrezzoGiornaliero(rs.getDouble("prezzo"));
        java.sql.Date ci = rs.getDate("check_in");
        java.sql.Date co = rs.getDate("check_out");
        d.setCheckIn(ci != null ? ci.toString() : "");
        d.setCheckOut(co != null ? co.toString() : "");
        d.setStato(rs.getString("status"));
        d.setNote(rs.getString("note"));
        d.setGiorni(rs.getInt("giorni"));
        d.setTotale(rs.getDouble("totale"));
        return d;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getString("reservation_id"));
        r.setUser(rs.getString("user_id"));
        r.setCommittente(rs.getInt("committente_id"));
        r.setRoom(rs.getInt("room_id"));
        r.setCheckIn(rs.getDate("check_in"));
        r.setCheckOut(rs.getDate("check_out"));
        r.setGiorni(rs.getInt("giorni"));
        r.setStato(ReservationStatus.valueOf(rs.getString("status").toUpperCase()));
        r.setNote(rs.getString("note"));
        return r;
    }

    private static java.sql.Date toSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }
}
