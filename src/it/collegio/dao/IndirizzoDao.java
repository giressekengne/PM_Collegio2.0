package it.collegio.dao;

import it.collegio.models.Indirizzo;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndirizzoDao {

    private DatabaseConnection dbConnection;

    public IndirizzoDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public List<Indirizzo> getAll() {
        List<Indirizzo> indirizzi = new ArrayList<>();
        String query = QueryContainer.queryIndirizzo;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                indirizzi.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(IndirizzoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return indirizzi;
    }

    public Indirizzo getByVia(String via) {
        Indirizzo indirizzo = null;
        String query = QueryContainer.queryGetIndirizzoByVia;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, via);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    indirizzo = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IndirizzoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return indirizzo;
    }

    public Indirizzo getById(int id) {
        Indirizzo indirizzo = null;
        String query = QueryContainer.queryGetIndirizzoById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    indirizzo = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IndirizzoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return indirizzo;
    }

    public int insertIndirizzo(Indirizzo indirizzo) {
        String query = QueryContainer.queryInsIndirizzo;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, indirizzo.getPaese());
            pst.setString(2, indirizzo.getProvincia());
            pst.setString(3, indirizzo.getCitta());
            pst.setString(4, indirizzo.getVia());
            pst.setInt(5, indirizzo.getCap());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IndirizzoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean updateIndirizzo(Indirizzo indirizzo) {
        String query = QueryContainer.queryUpdIndirizzo;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setString(1, indirizzo.getPaese());
            pst.setString(2, indirizzo.getProvincia());
            pst.setString(3, indirizzo.getCitta());
            pst.setString(4, indirizzo.getVia());
            pst.setInt(5, indirizzo.getCap());
            pst.setInt(6, indirizzo.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(IndirizzoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private Indirizzo mapRow(ResultSet rs) throws SQLException {
        Indirizzo i = new Indirizzo();
        i.setId(rs.getInt("indirizzo_id"));
        i.setPaese(rs.getString("paese"));
        i.setProvincia(rs.getString("provincia"));
        i.setCitta(rs.getString("citta"));
        i.setVia(rs.getString("via"));
        i.setCap(rs.getInt("cap"));
        return i;
    }
}
