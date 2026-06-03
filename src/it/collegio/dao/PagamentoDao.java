package it.collegio.dao;

import it.collegio.models.Pagamento;
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

public class PagamentoDao {

    private DatabaseConnection dbConnection;

    public PagamentoDao() {
        this.dbConnection = DatabaseConnection.getDatabaseConnection();
    }

    public int insertPagamento(Pagamento pagamento) {
        String query = QueryContainer.queryInsPagamento;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pst.setInt(1, pagamento.getFatturaId());
            pst.setInt(2, pagamento.getMetodoId());
            pst.setDouble(3, pagamento.getImporto());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pst.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PagamentoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public List<Pagamento> getByFattura(int fatturaId) {
        List<Pagamento> pagamenti = new ArrayList<>();
        String query = QueryContainer.queryGetPagamentiByFattura;

        try (PreparedStatement pst = dbConnection.getConnection().prepareStatement(query)) {
            pst.setInt(1, fatturaId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    pagamenti.add(mapRow(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PagamentoDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pagamenti;
    }

    private Pagamento mapRow(ResultSet rs) throws SQLException {
        Pagamento p = new Pagamento();
        p.setId(rs.getInt("pagamento_id"));
        p.setFatturaId(rs.getInt("fattura_id"));
        p.setMetodoId(rs.getInt("metodo_id"));
        p.setImporto(rs.getDouble("importo"));

        Timestamp data = rs.getTimestamp("data_pagamento");
        if (data != null) {
            p.setDataPagamento(data.toLocalDateTime());
        }
        return p;
    }
}
