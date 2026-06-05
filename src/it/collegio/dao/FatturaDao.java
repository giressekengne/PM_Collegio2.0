package it.collegio.dao;

import it.collegio.dto.FatturaDettaglio;
import it.collegio.enums.FatturaStatus;
import it.collegio.models.Fattura;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FatturaDao {

    private DatabaseConnection dbConnection;

    public FatturaDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public int insertFattura(Fattura fattura) {
        String query = QueryContainer.queryInsFattura;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, fattura.getReservationId());
            pst.setDouble(2, fattura.getImporto());
            LocalDateTime data = fattura.getDataEmissione() != null ? fattura.getDataEmissione() : LocalDateTime.now();
            pst.setTimestamp(3, Timestamp.valueOf(data));
            pst.setString(4, fattura.getStato() != null ? fattura.getStato().getDbValue() : FatturaStatus.NON_PAGATA.getDbValue());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean updateFattura(Fattura fattura) {
        String query = QueryContainer.queryUpdFattura;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setDouble(1, fattura.getImporto());
            LocalDateTime data = fattura.getDataEmissione() != null ? fattura.getDataEmissione() : LocalDateTime.now();
            pst.setTimestamp(2, Timestamp.valueOf(data));
            pst.setString(3, fattura.getStato() != null ? fattura.getStato().getDbValue() : FatturaStatus.NON_PAGATA.getDbValue());
            pst.setInt(4, fattura.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean updateStato(int fatturaId, FatturaStatus stato) {
        String query = QueryContainer.queryUpdFatturaStato;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, stato.getDbValue());
            pst.setInt(2, fatturaId);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Fattura getById(int id) {
        Fattura fattura = null;
        String query = QueryContainer.queryGetFatturaById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    fattura = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fattura;
    }

    public Fattura getByReservation(int reservationId) {
        Fattura fattura = null;
        String query = QueryContainer.queryGetFatturaByReservation;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, reservationId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    fattura = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fattura;
    }

    public List<Fattura> getAll() {
        List<Fattura> fatture = new ArrayList<>();
        String query = QueryContainer.queryGetFatture;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                fatture.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fatture;
    }

    public FatturaDettaglio getDettaglio(int fatturaId) {
        FatturaDettaglio dettaglio = null;
        String query = QueryContainer.queryGetFatturaDettaglio;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, fatturaId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    dettaglio = mapDettaglio(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettaglio;
    }

    public List<FatturaDettaglio> getDettagli() {
        List<FatturaDettaglio> dettagli = new ArrayList<>();
        String query = QueryContainer.queryGetFattureDettagliate;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                dettagli.add(mapDettaglio(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettagli;
    }

    public List<FatturaDettaglio> getDettagliByUser(String userCounter) {
        List<FatturaDettaglio> dettagli = new ArrayList<>();
        String query = QueryContainer.queryGetFattureDettagliateByUser;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, userCounter);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    dettagli.add(mapDettaglio(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(FatturaDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettagli;
    }

    private FatturaDettaglio mapDettaglio(ResultSet rs) throws SQLException {
        FatturaDettaglio d = new FatturaDettaglio();
        d.setFatturaId(rs.getInt("fattura_id"));
        d.setReservationId(rs.getInt("reservation_id"));
        d.setClienteNome(rs.getString("nome"));
        d.setNumeroStanza(rs.getInt("numero_stanza"));
        d.setImporto(rs.getDouble("importo"));
        Timestamp data = rs.getTimestamp("data_emissione");
        if (data != null) {
            d.setDataEmissione(data.toLocalDateTime());
        }
        d.setStato(FatturaStatus.fromDb(rs.getString("stato")));
        d.setReservationStato(rs.getString("status"));
        return d;
    }

    private Fattura mapRow(ResultSet rs) throws SQLException {
        Fattura f = new Fattura();
        f.setId(rs.getInt("fattura_id"));
        f.setReservationId(rs.getInt("reservation_id"));
        f.setImporto(rs.getDouble("importo"));

        Timestamp data = rs.getTimestamp("data_emissione");
        if (data != null) {
            f.setDataEmissione(data.toLocalDateTime());
        }
        f.setStato(FatturaStatus.fromDb(rs.getString("stato")));
        return f;
    }
}
