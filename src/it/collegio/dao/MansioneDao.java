package it.collegio.dao;

import it.collegio.models.Mansione;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MansioneDao {

    private DatabaseConnection dbConnection;

    public MansioneDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public List<Mansione> getAll() {
        List<Mansione> mansioni = new ArrayList<>();
        String query = QueryContainer.queryMansione;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                mansioni.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MansioneDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mansioni;
    }

    public Mansione getById(int id) {
        Mansione mansione = null;
        String query = QueryContainer.queryGetRoleById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    mansione = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MansioneDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mansione;
    }

    public Mansione getByType(String roleType) {
        Mansione mansione = null;
        String query = QueryContainer.queryGetMansioneByType;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setString(1, roleType);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    mansione = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MansioneDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mansione;
    }

    private Mansione mapRow(ResultSet rs) throws SQLException {
        Mansione m = new Mansione();
        m.setId(rs.getInt("role_id"));
        m.setTipo(rs.getString("role_type"));
        m.setNome(rs.getString("role_nome"));
        return m;
    }
}
