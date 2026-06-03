package it.collegio.controllers;

import it.collegio.dao.DatabaseConnection;
import it.collegio.dao.ReservationDao;
import it.collegio.dao.StoricoPrenotazioniDao;
import it.collegio.dto.ReservationDettaglio;
import it.collegio.models.Reservation;
import it.collegio.models.StoricoPrenotazioni;
import it.collegio.utilities.SessionContext;
import it.collegio.utilities.utility;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationController {

    private final ReservationDao reservationDao;
    private final StoricoPrenotazioniDao storicoDao;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public ReservationController() {
        this.reservationDao = new ReservationDao();
        this.storicoDao = new StoricoPrenotazioniDao();
    }

    public List<ReservationDettaglio> getDettagliPerRuolo() {
        if ("U".equalsIgnoreCase(SessionContext.roleType)) {
            return reservationDao.getDettagliByUser(SessionContext.userCounter);
        }
        return reservationDao.getDettagli();
    }

    public ReservationDettaglio cercaDettaglio(int reservationId) {
        String userFilter = "U".equalsIgnoreCase(SessionContext.roleType)
                ? SessionContext.userCounter
                : null;
        return reservationDao.getDettaglio(reservationId, userFilter);
    }

    /**
     * Aggiorna una prenotazione (check_in, check_out, note) e registra le
     * vecchie date in StoricoPrenotazioni per audit. Tutto in transazione.
     * Ricalcola i giorni server-side.
     *
     * @return true se update + storico vanno a buon fine
     */
    public boolean aggiornaConStorico(int reservationId,
                                       String newCheckInStr,
                                       String newCheckOutStr,
                                       String note) {
        Connection conn = DatabaseConnection.getDatabaseConnection().getConnection();

        try {
            Reservation existing = reservationDao.getById(String.valueOf(reservationId));
            if (existing == null) {
                return false;
            }

            Date newCheckIn = parseDate(newCheckInStr);
            Date newCheckOut = parseDate(newCheckOutStr);
            if (newCheckIn == null) {
                return false;
            }

            int giorni;
            if (newCheckOut != null) {
                giorni = utility.calculDate(newCheckInStr, newCheckOutStr);
            } else {
                giorni = existing.getGiorni();
            }

            conn.setAutoCommit(false);

            Date oldCheckIn = existing.getCheckIn();
            Date oldCheckOut = existing.getCheckOut();
            String userId = existing.getUser();

            existing.setCheckIn(newCheckIn);
            existing.setCheckOut(newCheckOut);
            existing.setNote(note);
            existing.setGiorni(giorni);

            if (!reservationDao.updateReservation(existing)) {
                conn.rollback();
                return false;
            }

            StoricoPrenotazioni storico = new StoricoPrenotazioni(
                    reservationId, userId,
                    oldCheckIn, oldCheckOut,
                    newCheckIn, newCheckOut);
            storico.setDataModifica(new Date());

            if (storicoDao.insertStorico(storico) < 0) {
                conn.rollback();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException ex) {
            Logger.getLogger(ReservationController.class.getName()).log(Level.SEVERE, null, ex);
            try {
                conn.rollback();
            } catch (SQLException rbEx) {
                Logger.getLogger(ReservationController.class.getName()).log(Level.SEVERE, null, rbEx);
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(ReservationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return SDF.parse(dateStr.trim());
        } catch (ParseException ex) {
            return null;
        }
    }
}
