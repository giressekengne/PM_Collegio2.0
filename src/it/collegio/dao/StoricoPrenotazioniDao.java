package it.collegio.dao;

import it.collegio.models.StoricoPrenotazioni;
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

public class StoricoPrenotazioniDao {

    private DatabaseConnection dbConnection;

    public StoricoPrenotazioniDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public int insertStorico(StoricoPrenotazioni storico) {
        String query = QueryContainer.queryInsStorico;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, storico.getReservationId());
            pst.setString(2, storico.getUserId());
            pst.setDate(3, toSqlDate(storico.getCheckInPrecedente()));
            pst.setDate(4, toSqlDate(storico.getCheckOutPrecedente()));
            pst.setDate(5, toSqlDate(storico.getNuovoCheckIn()));
            pst.setDate(6, toSqlDate(storico.getNuovoCheckOut()));
            pst.setDate(7, toSqlDate(storico.getDataModifica() != null ? storico.getDataModifica() : new Date()));

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StoricoPrenotazioniDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public List<StoricoPrenotazioni> getByReservation(int reservationId) {
        List<StoricoPrenotazioni> storici = new ArrayList<>();
        String query = QueryContainer.queryGetStoricoByReservation;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, reservationId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    storici.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StoricoPrenotazioniDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return storici;
    }

    private StoricoPrenotazioni mapRow(ResultSet rs) throws SQLException {
        StoricoPrenotazioni s = new StoricoPrenotazioni();
        s.setId(rs.getInt("storico_id"));
        s.setReservationId(rs.getInt("reservation_id"));
        s.setUserId(rs.getString("user_id"));
        s.setCheckInPrecedente(rs.getDate("check_in_precedente"));
        s.setCheckOutPrecedente(rs.getDate("check_out_precedente"));
        s.setNuovoCheckIn(rs.getDate("nuovo_check_in"));
        s.setNuovoCheckOut(rs.getDate("nuovo_check_out"));
        s.setDataModifica(rs.getDate("data_modifica"));
        return s;
    }

    private static java.sql.Date toSqlDate(Date date) {
        return date != null ? new java.sql.Date(date.getTime()) : null;
    }
}
