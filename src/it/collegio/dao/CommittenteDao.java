package it.collegio.dao;

import it.collegio.dto.CommittenteDettaglio;
import it.collegio.models.Committente;
import it.collegio.models.User;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommittenteDao {

    private DatabaseConnection dbConnection;

    public CommittenteDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public List<Committente> getAll() {
        List<Committente> committenti = new ArrayList<>();
        String query = QueryContainer.queryGetCommittenti;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                committenti.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return committenti;
    }

    public Committente getById(int id) {
        Committente committente = null;
        String query = QueryContainer.queryGetCommittenteById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    committente = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return committente;
    }

    public int insertCommittente(Committente committente) {
        String query = QueryContainer.queryInsCommittente;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setString(1, committente.getNome());
            pst.setString(2, committente.getAdmin() != null ? committente.getAdmin().getCounter() : null);
            pst.setString(3, committente.getEmail());
            pst.setString(4, committente.getMobile());
            pst.setInt(5, committente.getAddress());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean updateCommittente(Committente committente) {
        String query = QueryContainer.queryUpdCommittente;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {

            pst.setString(1, committente.getNome());
            pst.setString(2, committente.getAdmin() != null ? committente.getAdmin().getCounter() : null);
            pst.setString(3, committente.getEmail());
            pst.setString(4, committente.getMobile());
            pst.setInt(5, committente.getAddress());
            pst.setInt(6, committente.getId());

            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getMaxId() {
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryMaxCommittenteId);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                Object raw = rs.getObject(1);
                if (raw != null) {
                    return ((Number) raw).intValue();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public boolean insertCommittenteWithId(Committente committente) {
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryInsCommittenteFull)) {
            pst.setInt(1, committente.getId());
            pst.setString(2, committente.getNome());
            pst.setString(3, committente.getAdmin() != null ? committente.getAdmin().getCounter() : null);
            pst.setString(4, committente.getEmail());
            pst.setString(5, committente.getMobile());
            pst.setInt(6, committente.getAddress());
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<CommittenteDettaglio> getDettagli() {
        List<CommittenteDettaglio> dettagli = new ArrayList<>();
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryGetCommittentiDettagli);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                dettagli.add(mapDettaglio(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dettagli;
    }

    public CommittenteDettaglio getDettaglio(int id) {
        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(QueryContainer.queryGetCommittenteDettaglio)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapDettaglio(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CommittenteDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private CommittenteDettaglio mapDettaglio(ResultSet rs) throws SQLException {
        CommittenteDettaglio d = new CommittenteDettaglio();
        d.setCodCommittente(rs.getInt("codCommittente"));
        d.setRagioneSociale(rs.getString("ragione_Sociale"));
        d.setGestoreEmail(rs.getString("gestore_email"));
        d.setEmail(rs.getString("email"));
        d.setTelefono(rs.getString("telefono"));
        d.setVia(rs.getString("via"));
        return d;
    }

    private Committente mapRow(ResultSet rs) throws SQLException {
        Committente c = new Committente();
        c.setId(rs.getInt("codCommittente"));
        c.setNome(rs.getString("ragione_Sociale"));
        c.setEmail(rs.getString("email"));
        c.setMobile(rs.getString("telefono"));
        c.setAddress(rs.getInt("indirizzo_id"));

        String gestoreId = rs.getString("gestore");
        if (gestoreId != null) {
            User shell = new User();
            shell.setCounter(gestoreId);
            c.setAdmin(shell);
        }
        return c;
    }
}
