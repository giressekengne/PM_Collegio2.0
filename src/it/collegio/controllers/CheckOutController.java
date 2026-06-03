package it.collegio.controllers;

import it.collegio.dao.DatabaseConnection;
import it.collegio.dao.FatturaDao;
import it.collegio.dao.ReservationDao;
import it.collegio.dao.RoomDao;
import it.collegio.dao.UserDao;
import it.collegio.dto.CheckoutPreview;
import it.collegio.enums.FatturaStatus;
import it.collegio.enums.ReservationStatus;
import it.collegio.enums.RoomStatus;
import it.collegio.models.Fattura;
import it.collegio.models.Reservation;
import it.collegio.models.Room;
import it.collegio.models.User;
import it.collegio.utilities.utility;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CheckOutController {

    private final ReservationDao reservationDao;
    private final RoomDao roomDao;
    private final UserDao userDao;
    private final FatturaDao fatturaDao;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    public CheckOutController() {
        this.reservationDao = new ReservationDao();
        this.roomDao = new RoomDao();
        this.userDao = new UserDao();
        this.fatturaDao = new FatturaDao();
    }

    public CheckoutPreview getCheckoutPreview(int reservationId, String checkOutDateStr) {
        Reservation reservation = reservationDao.getById(String.valueOf(reservationId));
        if (reservation == null) {
            return null;
        }

        Room room = roomDao.getById(reservation.getRoom());
        User user = userDao.getByCounter(reservation.getUser());
        if (room == null || user == null) {
            return null;
        }

        String checkInStr = reservation.getCheckIn() != null ? SDF.format(reservation.getCheckIn()) : "";
        int giorni = utility.calculDate(checkInStr, checkOutDateStr);
        double totale = room.getPrezzo() * giorni;

        CheckoutPreview preview = new CheckoutPreview();
        preview.setReservationId(reservationId);
        preview.setUserEmail(user.getEmail());
        preview.setUserNome(user.getNome());
        preview.setUserMobile(user.getMobile());
        preview.setRoomId(room.getId());
        preview.setPrezzoGiornaliero(room.getPrezzo());
        preview.setCheckInDate(checkInStr);
        preview.setCheckOutDate(checkOutDateStr);
        preview.setGiorni(giorni);
        preview.setTotale(totale);

        return preview;
    }

    /**
     * Esegue il checkout in transazione: UPDATE Reservation (status=completata,
     * check_out, giorni), UPDATE Room (stato=disponibile), upsert Fattura.
     * Ricalcola tutti gli importi server-side (non si fida della view).
     *
     * @return l'id della Fattura emessa/aggiornata, o -1 se errore
     */
    public int eseguiCheckout(int reservationId, String checkOutDateStr) {
        Connection conn = DatabaseConnection.getDatabaseConnection().getConnection();

        try {
            Reservation reservation = reservationDao.getById(String.valueOf(reservationId));
            if (reservation == null) {
                return -1;
            }
            Room room = roomDao.getById(reservation.getRoom());
            if (room == null) {
                return -1;
            }

            Date checkOutDate;
            try {
                checkOutDate = SDF.parse(checkOutDateStr.trim());
            } catch (Exception ex) {
                return -1;
            }

            String checkInStr = reservation.getCheckIn() != null ? SDF.format(reservation.getCheckIn()) : "";
            int giorni = utility.calculDate(checkInStr, checkOutDateStr);
            double importo = room.getPrezzo() * giorni;

            conn.setAutoCommit(false);

            reservation.setCheckOut(checkOutDate);
            reservation.setStato(ReservationStatus.COMPLETATA);
            reservation.setNote("Camera libera");
            reservation.setGiorni(giorni);
            if (!reservationDao.updateReservation(reservation)) {
                conn.rollback();
                return -1;
            }

            if (!roomDao.updateStato(room.getId(), RoomStatus.DISPONIBILE)) {
                conn.rollback();
                return -1;
            }

            int fatturaId = upsertFattura(reservationId, importo);
            if (fatturaId < 0) {
                conn.rollback();
                return -1;
            }

            conn.commit();
            return fatturaId;

        } catch (SQLException ex) {
            Logger.getLogger(CheckOutController.class.getName()).log(Level.SEVERE, null, ex);
            try {
                conn.rollback();
            } catch (SQLException rbEx) {
                Logger.getLogger(CheckOutController.class.getName()).log(Level.SEVERE, null, rbEx);
            }
            return -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                Logger.getLogger(CheckOutController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private int upsertFattura(int reservationId, double importo) {
        Fattura existing = fatturaDao.getByReservation(reservationId);
        if (existing != null) {
            existing.setImporto(importo);
            existing.setDataEmissione(LocalDateTime.now());
            existing.setStato(FatturaStatus.IN_ATTESA);
            return fatturaDao.updateFattura(existing) ? existing.getId() : -1;
        }
        Fattura nuova = new Fattura(reservationId, importo, LocalDateTime.now(), FatturaStatus.IN_ATTESA);
        return fatturaDao.insertFattura(nuova);
    }
}
