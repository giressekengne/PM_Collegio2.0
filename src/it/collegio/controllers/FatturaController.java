package it.collegio.controllers;

import it.collegio.dao.FatturaDao;
import it.collegio.dao.MetodoPagamentoDao;
import it.collegio.dao.PagamentoDao;
import it.collegio.dto.FatturaDettaglio;
import it.collegio.enums.FatturaStatus;
import it.collegio.models.Fattura;
import it.collegio.models.MetodoPagamento;
import it.collegio.models.Pagamento;
import it.collegio.utilities.SessionContext;
import java.util.List;

public class FatturaController {

    private final FatturaDao fatturaDao;
    private final PagamentoDao pagamentoDao;
    private final MetodoPagamentoDao metodoPagamentoDao;

    public FatturaController() {
        this.fatturaDao = new FatturaDao();
        this.pagamentoDao = new PagamentoDao();
        this.metodoPagamentoDao = new MetodoPagamentoDao();
    }

    public FatturaDettaglio getDettaglio(int fatturaId) {
        return fatturaDao.getDettaglio(fatturaId);
    }

    public List<FatturaDettaglio> getDettagliPerRuolo() {
        if ("U".equalsIgnoreCase(SessionContext.roleType)) {
            return fatturaDao.getDettagliByUser(SessionContext.userCounter);
        }
        return fatturaDao.getDettagli();
    }

    public List<MetodoPagamento> getMetodiPagamento() {
        return metodoPagamentoDao.getAll();
    }

    public boolean paga(int fatturaId, int metodoId) {
        Fattura fattura = fatturaDao.getById(fatturaId);
        if (fattura == null) {
            return false;
        }

        Pagamento pagamento = new Pagamento(fatturaId, metodoId, fattura.getImporto());
        int pagamentoId = pagamentoDao.insertPagamento(pagamento);
        if (pagamentoId < 0) {
            return false;
        }

        return fatturaDao.updateStato(fatturaId, FatturaStatus.PAGATA);
    }

    public boolean annulla(int fatturaId) {
        return fatturaDao.updateStato(fatturaId, FatturaStatus.NON_PAGATA);
    }

    public boolean isPagabile(FatturaStatus stato) {
        return stato == FatturaStatus.IN_ATTESA || stato == FatturaStatus.NON_PAGATA;
    }
}
