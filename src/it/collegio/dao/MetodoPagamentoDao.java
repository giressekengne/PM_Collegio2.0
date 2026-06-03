package it.collegio.dao;

import it.collegio.models.MetodoPagamento;
import it.collegio.utilities.QueryContainer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetodoPagamentoDao {

    private DatabaseConnection dbConnection;

    public MetodoPagamentoDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public List<MetodoPagamento> getAll() {
        List<MetodoPagamento> metodi = new ArrayList<>();
        String query = QueryContainer.queryGetMetodiPagamento;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                metodi.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MetodoPagamentoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metodi;
    }

    public MetodoPagamento getById(int id) {
        MetodoPagamento metodo = null;
        String query = QueryContainer.queryGetMetodoPagamentoById;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    metodo = mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MetodoPagamentoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metodo;
    }

    private MetodoPagamento mapRow(ResultSet rs) throws SQLException {
        MetodoPagamento m = new MetodoPagamento();
        m.setId(rs.getInt("metodo_id"));
        m.setNome(rs.getString("nome"));
        m.setDescrizione(rs.getString("descrizione"));
        return m;
    }
}
